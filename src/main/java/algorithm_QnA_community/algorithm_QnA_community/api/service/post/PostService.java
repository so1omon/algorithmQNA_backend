package algorithm_QnA_community.algorithm_QnA_community.api.service.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentWithIsLikeDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentsRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.TopCommentRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.*;
import algorithm_QnA_community.algorithm_QnA_community.api.service.comment.CommentService;
import algorithm_QnA_community.algorithm_QnA_community.api.service.s3.S3Service;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikePost;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import algorithm_QnA_community.algorithm_QnA_community.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode.WRONG_POST_ID;
import static algorithm_QnA_community.algorithm_QnA_community.domain.member.Role.ROLE_USER;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.post
 * fileName       : PostService
 * author         : janguni
 * date           : 2023/05/11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/11        janguni            최초 생성
 * 2023/05/19        janguni            중복 코드 checkNoticePermission(), getPostById(), checkPostAccessPermission()로 추출
 * 2023/05/19        solmin             게시글 작성 메소드 리턴타입 변경
 * 2023/05/19        solmin             TODO 메세지 작성
 * 2023/05/21        janguni            게시물 조회 시 추천/비추천(게시물) 정보 코드 추가,
 *                                                  추천/비추천(댓글) 정보 코드 수정
 * 2023/05/24        janguni            게시물 조회 시 해당 게시물 조회수+1 처리
 * 2023/05/26        solmin             좋아요 정보 삭제 시 연관관계 끊는 메소드 수행
 *                                      (원래 안건드릴려고 했는데 likeComment랑 너무 겹치는 내용이라 수정했어요 ㅜㅜ)
 * 2023/05/30        janguni            게시물 등록, 수정, 조회 시 keyWords 관련 코드 추가
 * 2023/06/01        janguni            게시물 목록 조회 코드 수정 (필터 적용)
 * 2023/06/01        solmin             게시글 작성 시 임시 경로에 존재하는 이미지 정보 삭제
 * 2023/06/11        janguni            댓글 하이라이팅 기능 추가 (리펙토링 예정)
 * 2023/06/15        janguni            게시물 조회 response에 채택된 댓글 추가
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    static final int MAX_POST_SIZE = 20;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final LikePostRepository likePostRepository;
    private final ReportPostRepository reportPostRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final S3Service s3Service;

    /**
     * 게시물 등록
     */
    @Transactional
    public PostWriteRes writePost(PostCreateReq postCreateReq, Member member){

        // 일반 사용자가 공지사항 타입을 선택한 경우
        checkNoticePermission(member.getRole(), postCreateReq.getPostType());

        List<String> keyWords = postCreateReq.getKeyWords();

        if (keyWords == null || keyWords.isEmpty()) {
            keyWords = new ArrayList<>();
            keyWords.add("Unknown");
        }

        Post post = Post.createPost()
            .member(member)
            .title(postCreateReq.getTitle())
            .content(postCreateReq.getContent())
            .postCategory(PostCategory.valueOf(postCreateReq.getPostCategory()))
            .type(PostType.valueOf(postCreateReq.getPostType()))
            .keyWords(keyWords)
            .build();

        Post savedPost = postRepository.save(post);

        savedPost.updateContent(
            s3Service.moveImages(savedPost.getId(),
                savedPost.getContent(),
                postCreateReq.getImageIds(), S3Service.POST_DIR));

        return new PostWriteRes(savedPost.getId(), savedPost.getCreatedDate());
    }

    /**
     * 게시물 수정
     */
    @Transactional
    public void updatePost(Long postId,PostUpdateReq postUpdateReq, Member member) {

        // 일반 사용자가 공지사항 타입을 선택한 경우
        checkNoticePermission(member.getRole(), postUpdateReq.getPostType());

        Post findPost = getPost(postId);

        // 본인이 쓴 게시물이 맞는지 확인
        checkPostAccessPermission(member != findPost.getMember(), ErrorCode.UNAUTHORIZED, "게시물을 수정할 권한이 없습니다.");

        setIfNotNull(postUpdateReq.getTitle(), findPost::updateTitle);
        setIfNotNull(postUpdateReq.getContent(), findPost::updateContent);
        setIfNotNull(PostCategory.valueOf(postUpdateReq.getPostCategory()), findPost::updateCategory);
        setIfNotNull(PostType.valueOf(postUpdateReq.getPostType()),findPost::updateType);
        setIfNotNull(postUpdateReq.getKeyWords(), findPost::updateKeyWords);
    }

    /**
     * 게시물 삭제
     */
    @Transactional
    public void deletePost(Long postId, Member member) {
        Post findPost = getPost(postId);

        // 본인이 쓴 게시물이 맞는지 확인
        checkPostAccessPermission(member != findPost.getMember(), ErrorCode.UNAUTHORIZED, "게시물을 삭제할 권한이 없습니다.");
        findPost.deletePost();
        postRepository.delete(findPost);
    }

    private void checkPostAccessPermission(boolean member, ErrorCode unauthorized, String message) {
        if (member){
            throw new CustomException(unauthorized, message);
        }
    }

    /**
     * 게시물 추천
     */
    @Transactional
    public void likePost(Long postId, LikeReq postLikeReq, Member member) {

        Post findPost = getPost(postId);

        Optional<LikePost> findLikePost = likePostRepository.findByPostIdAndMemberId(postId, member.getId());

        if (postLikeReq.getCancel()){
            if (!findLikePost.isPresent()) log.info("추천정보가 존재하지 않음");
            else {
                findPost.updateLikeCnt(postLikeReq.getIsLike(), false);
                findLikePost.get().deleteLikePost();
                likePostRepository.delete(findLikePost.get());
            }
        }

        else {
            if (!findLikePost.isPresent()){
                LikePost likePost = LikePost.createLikePost()
                    .member(member)
                    .post(findPost)
                    .isLike(postLikeReq.getIsLike())
                    .build();

                likePostRepository.save(likePost);
            }
            else {
                findLikePost.get().updateState(postLikeReq.getIsLike());

            }
        }
    }


    /**
     * 게시물 신고
     */
    @Transactional
    public void reportPost(Long postId, ReportReq postReportReq, Member member) {

        Post findPost = getPost(postId);

        // 본인 게시물을 신고하려는 경우
        checkPostAccessPermission(member == findPost.getMember(), ErrorCode.REPORT_MY_RESOURCE, "자신이 작성한 게시물은 신고할 수 없습니다.");

        Optional<ReportPost> findReportPost = reportPostRepository.findByPostIdAndMemberId(postId, member.getId());

        if (!findReportPost.isPresent()){ // 해당 게시물을 신고한 적이 없다면
            ReportPost reportPost = ReportPost.createReportPost()
                .post(findPost)
                .member(member)
                .reportCategory( ReportCategory.valueOf(postReportReq.getCategory()))
                .detail(postReportReq.getDetail())
                .build();

            reportPostRepository.save(reportPost);
        } else{
            // 신고 카테고리, 신고사유 업데이트
            findReportPost.get().updateReportInfo(ReportCategory.valueOf(postReportReq.getCategory()), postReportReq.getDetail());
        }
    }

    /**
     * 상세 게시물 조회
     */
    @Transactional
    public PostDetailRes readPostDetail(Long postId, Member member){

        //**** 게시물 정보 ****//
        Post findPost = getPost(postId); // 게시물
        Boolean isLikedPost = checkPostLike(postId, member); // 게시물 추천 정보

        // 게시물 조회수 + 1
        findPost.updateViews();

        //**** 작성자 정보 ****//
        Member postingMember = memberRepository.findById(findPost.getMember().getId()).get();


        //**** 댓글 정보 ****//
        CommentsRes commentsRes = commentService.getComments(postId, 0, member.getId());
        CommentRes pinnedCommentRes = getPinnedCommentRes(member, findPost);

        // 총 댓글 갯수
        int totalCommentSize = commentRepository.countCommentByPostId(findPost.getId());

        //**** Response 객체 생성 ****//
        PostDetailRes postDetailRes =
            new PostDetailRes(findPost, postingMember, isLikedPost, commentsRes, totalCommentSize, pinnedCommentRes);
        return postDetailRes;
    }


    private CommentRes getPinnedCommentRes(Member member, Post findPost) {
        Optional<Comment> pinnedComment = commentRepository.findPinnedCommentByPost(findPost.getId());
        if (pinnedComment.isEmpty()) return null;
        Boolean isLikedPinnedComment = commentRepository.getLikeStatusByMemberAndComment(member, pinnedComment.get());
        CommentRes pinnedCommentRes = new CommentRes(pinnedComment.get(), isLikedPinnedComment);
        return pinnedCommentRes;
    }

    // 해당 게시물의 사용자가 추천을 했는지
    private Boolean checkPostLike(Long postId, Member member) {
        Optional<LikePost> findLikePost = likePostRepository.findByPostIdAndMemberId(postId, member.getId());
        Boolean isLikedPost;
        if (findLikePost.isPresent()){
            isLikedPost = findLikePost.get().isLike();
        } else {
            isLikedPost = null;
        }
        return isLikedPost;
    }

    /**
     * 게시물 목록 조회
     */
    public PostsResultRes readPosts(PostSearchDto postSearchDto){
        Page<PostSimpleDto> pagePosts = null;

        switch (postSearchDto.getPostSort()) {
            case LATESTDESC: // 최신순
                log.info("최신순");
                pagePosts = postRepository.findPostsOrderByCreatedDateDesc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case LATESTASC: // 오래된 순
                log.info("오래된 순");
                pagePosts = postRepository.findPostsOrderByCreatedDateAsc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case COMMENTCNTASC: // 댓글 오름차순
                log.info("댓글 오름차순");
                pagePosts = postRepository.findPostsOrderByCommentSizeAsc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case COMMENTCNTDESC: // 댓글 내림차순
                log.info("댓글 내림차순");
                pagePosts = postRepository.findPostsOrderByCommentSizeDesc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case LIKEASC:   // 추천 오름차순
                log.info("추천 오름차순");
                pagePosts = postRepository.findPostsOrderByLikeAsc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case LIKEDESC:  // 추천 내림차순
                log.info("추천 내림차순");
                pagePosts = postRepository.findPostsOrderByLikeDesc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case VIEWCNTASC:    // 조회수 오름차순
                log.info("조회수 오름차순");
                pagePosts = postRepository.findPostsOrderByViewAsc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case VIEWCNTDESC:   // 조회수 내림차순
                log.info("조회수 내림차순");
                pagePosts = postRepository.findPostsOrderByViewDesc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case POPULAR:   // 인기순
                log.info("인기순");
                pagePosts = postRepository.findPostsOrderByPopularDesc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
        }

        // 게시물 데이터
        List<PostSimpleDto> posts = pagePosts.getContent();
        int postSize = posts.size();

        if (postSize==0 & postSearchDto.getPage()!=0) { // 존재하지 않은 페이지 번호 일 경우
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않은 페이지 번호 입니다.");
        }

        // 페이징 정보
        int totalPageCount = pagePosts.getTotalPages(); // 전체 페이지 수
        boolean prev = pagePosts.hasPrevious(); // 전 페이지 유무
        boolean next = pagePosts.hasNext(); // 후 페이지 유무
        int curPageNumber = pagePosts.getNumber();

        List<PostSimpleDto> postSimpleContent = pagePosts.getContent();
        List<PostSimpleRes> postSimpleResList = new ArrayList<>();
        for (PostSimpleDto pDto:postSimpleContent) {
            PostSimpleRes postSimpleRes = new PostSimpleRes(pDto);
            postSimpleResList.add(postSimpleRes);
        }

        PostsResultRes postsResultRes = new PostsResultRes(curPageNumber, totalPageCount, next, prev, postSize, postSimpleResList);
        return postsResultRes;
    }

    /**
     * 댓글 하이라이팅
     */
    @Transactional
    public PostDetailWithHighlightCommentRes readPostWithHighlightComment(Long postId, Long commentId, Member member) {

        // 게시물 정보
        Post findPost = getPost(postId);
        Boolean isLikedPost = checkPostLike(findPost.getId(), member);

        // 댓글 정보
        CommentRes pinnedCommentRes = getPinnedCommentRes(member, findPost);
        Comment highlightComment = getComment(commentId);
        log.info("highlightComment={}", highlightComment.getId());
        Comment topComment = null;
        Comment d1Comment = null;
        Comment d2Comment = null;


        if (highlightComment.getPost().getId()!= findPost.getId()) {
            throw new CustomException(WRONG_POST_ID, "하이라이팅 댓글의 게시물 id와 post_id가 불일치 합니다.");
        }

        int depth = highlightComment.getDepth();
        if (depth == 0) topComment = highlightComment;
        else if (depth==1) {
            topComment = highlightComment.getParent();
            d1Comment = highlightComment;
        }
        else {
            topComment = highlightComment.getParent().getParent();
            d1Comment = highlightComment.getParent();
            d2Comment = highlightComment;
        }

        log.info("topComment={}", topComment.getId());
        log.info("d1Comment={}", d1Comment);
        log.info("d2Commnet={}", d2Comment);

        // 최상위 댓글 10개 조회
        Page<CommentWithIsLikeDto> topCommentsPage = getTopCommentsPage(member, findPost, topComment);
        log.info("topComment={}", topCommentsPage.getNumber());
        log.info("topCommentDto={}", topCommentsPage.getContent());
        List<CommentWithIsLikeDto> topCommentsDto = topCommentsPage.getContent();
        for (CommentWithIsLikeDto c:topCommentsDto) {
            log.info("tc={}", c.getComment().getId());
        }


        List<TopCommentRes> comments = new ArrayList<>();

        // 자식 댓글 정보를 포함한 TopCommentRes 생성
        for (CommentWithIsLikeDto tc:topCommentsDto) {
            TopCommentRes topCommentRes = getTopCommentResWithTargetComment(tc, d1Comment, d2Comment,member, findPost);
            comments.add(topCommentRes);
        }

        CommentsRes commentsRes = new CommentsRes(findPost.getId(), comments, topCommentsPage.getNumber(), topCommentsPage.getTotalPages(), topCommentsPage.hasNext(), topCommentsPage.hasPrevious(), topCommentsPage.getSize());

        PostDetailWithHighlightCommentRes postDetailWithHighlightCommentRes = new PostDetailWithHighlightCommentRes(findPost, member, isLikedPost, pinnedCommentRes,commentsRes, comments.size(), highlightComment.getId());
        return postDetailWithHighlightCommentRes;
    }

    /** topCommentRes를 리턴하는 함수 **/
    // parentCommentWithIsLikeDto -> 상위 댓글
    // child1 -> 하위 댓글
    // child2 -> 하위 댓글의 댓글

    // (상위 댓글이 depth=0인 댓글이라는 의미는 x)
    //  Ex1)  상위 댓글 (depth=0), 하위 댓글(depth=1), 하위 댓글의 댓글(depth=2)
    //  Ex2)  상위 댓글 (depth=1), 하위 댓글(depth=2), 하위 댓글의 댓글(null)
    private TopCommentRes getTopCommentResWithTargetComment(CommentWithIsLikeDto parentCommentWithIsLikeDto, Comment child1, Comment child2, Member member, Post post){

        // parent 댓글 정보
        Comment parent = parentCommentWithIsLikeDto.getComment();
        Boolean parentCommentIsLiked = parentCommentWithIsLikeDto.getIsLiked();

        // child1의 댓글의 페이지 정보 구하기
        int childCommentPage = 0;
        Long topCommentId = parent.getId();
        if (child1!=null) {
            if (topCommentId == child1.getParent().getId()) {
                int childRowNumber = commentRepository.findChildCommentRowNumberByParentCommentId(child1.getId(), topCommentId);
                childCommentPage = (childRowNumber - 1) / 10;
            }
        }

        // child1의 댓글 정보
        Page<CommentWithIsLikeDto> childCommentsPage = commentRepository.findChildCommentWithIsLikeDto(member.getId(), post.getId(), topCommentId, PageRequest.of(childCommentPage, 10));
        List<CommentWithIsLikeDto> childCommentsDto = childCommentsPage.getContent();

        TopCommentRes topCommentRes = new TopCommentRes(parent, parentCommentIsLiked, childCommentsPage.getNumber(), childCommentsPage.hasNext(), childCommentsPage.hasPrevious(), childCommentsPage.getTotalPages());

        // 자식댓글 (child1, child2)을 CommentRes로 변형하여 topCommentRes의 childCommentList에 추가
        for (CommentWithIsLikeDto c: childCommentsDto) {
            if (child2!=null) {
                // child2의 부모댓글이 c 경우 c의 topCommentRes 생성
                if (c.getComment().getId()==child2.getParent().getId()) {
                    TopCommentRes tc = getTopCommentResWithTargetComment(c, child2, null, member, post);
                    topCommentRes.addChild(tc);
                }
                else {
                    CommentRes cr = new CommentRes(c.getComment(), c.getIsLiked());
                    int crChildSize = c.getComment().getChild().size();
                    if (crChildSize>0) cr.setHasChild(true);
                    topCommentRes.addChild(cr);
                }
            }
            else {
                CommentRes cr = new CommentRes(c.getComment(), c.getIsLiked());
                int crChildSize = c.getComment().getChild().size();
                if (crChildSize>0) cr.setHasChild(true);
                topCommentRes.addChild(cr);
            }
            log.info("cc={}", c.getComment().getId());
        }
        return topCommentRes;
    }

    private Page<CommentWithIsLikeDto> getTopCommentsPage(Member member, Post findPost, Comment topComment) {
        int topCommentRowNumber = commentRepository.findCommentRowNumberByCommentId(topComment.getId(), findPost.getId());
        log.info("topCommentRowNumber={}", topCommentRowNumber);
        int topCommentPage = (topCommentRowNumber-1) / 10;
        log.info("topCommentPage={}", topCommentPage);
        Page<CommentWithIsLikeDto> topCommentsPage = commentRepository.findTopCommentWithIsLikeDto(member.getId(), findPost.getId(), PageRequest.of(topCommentPage, 10));
        return topCommentsPage;
    }

    private Comment getComment(Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));
        return findComment;
    }

    private Post getPost(Long postId) {
        Post findPost = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));
        return findPost;
    }

    private void checkNoticePermission(Role role, String postType) {
        checkPostAccessPermission(role == ROLE_USER & postType.equals(PostType.NOTICE.toString()), ErrorCode.UNAUTHORIZED, "공지사항을 작성할 수 있는 권한이 없습니다.");
    }

    private <T> void setIfNotNull(T value, Consumer<T> setter){
        if (value != null) {
            setter.accept(value);
        }
    }



}

