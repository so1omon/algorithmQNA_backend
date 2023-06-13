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
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostSortType;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import algorithm_QnA_community.algorithm_QnA_community.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
        log.info("keyWords={}", keyWords);

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

        s3Service.moveImages(savedPost.getId(), postCreateReq.getImageIds(), S3Service.POST_DIR);

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
        checkPostAccessPermission(member != findPost.getMember(), ErrorCode.UNAUTHORIZED, "게시물을 삭제할 권한이 없습니다.");

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

        // 총 댓글 갯수
        int totalCommentSize = commentRepository.countCommentByPostId(findPost.getId());

        //**** Response 객체 생성 ****//
        PostDetailRes postDetailRes =
            new PostDetailRes(findPost, postingMember, isLikedPost, commentsRes, totalCommentSize);
        return postDetailRes;
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
    public PostsResultRes readPosts(PostSearchDto postSearchDto){
        Page<Post> pagePosts=null;

        switch (PostSortType.valueOf(postSearchDto.getPostSort())) {
            case LATESTDESC: // 최신순
                pagePosts = postRepository.findByPostCategoryAndTypeOrderByCreatedDateDesc(categoryName, postType, PageRequest.of(pageNumber, MAX_POST_SIZE));
                break;
            case LATESTASC: // 오래된 순
                pagePosts = postRepository.findByPostCategoryAndTypeOrderByCreatedDateAsc(categoryName, postType, PageRequest.of(pageNumber, MAX_POST_SIZE));
                break;
            case COMMENTCNTASC: // 댓글 오름차순
                pagePosts = postRepository.findPostOrderByCommentCntAsc(categoryName, postType, PageRequest.of(pageNumber, MAX_POST_SIZE));
                break;
            case COMMENTCNTDESC: // 댓글 내림차순
                pagePosts = postRepository.findPostOrderByCommentCntDesc(categoryName, postType, PageRequest.of(pageNumber, MAX_POST_SIZE));
                break;
            case LIKEASC:   // 추천 오름차순
                pagePosts = postRepository.findByPostCategoryOrderByLike_DislikeASC(categoryName, postType, PageRequest.of(pageNumber, MAX_POST_SIZE));
                break;
            case LIKEDESC:  // 추천 내림차순
                pagePosts = postRepository.findByPostCategoryOrderByLike_DislikeDESC(categoryName, postType, PageRequest.of(pageNumber, MAX_POST_SIZE));
                break;
            case VIEWCNTASC:    // 조회수 오름차순
                pagePosts = postRepository.findByPostCategoryAndTypeOrderByViewsAsc(categoryName, postType, PageRequest.of(pageNumber, MAX_POST_SIZE));
                break;
            case VIEWCNTDESC:   // 조회수 내림차순
                pagePosts = postRepository.findByPostCategoryAndTypeOrderByViewsDesc(categoryName, postType, PageRequest.of(pageNumber, MAX_POST_SIZE));
                break;
            case POPULAR:   // 인기순
                pagePosts = postRepository.findByPopular(categoryName, postType, PageRequest.of(pageNumber, MAX_POST_SIZE));
                break;
    }
     **/

    /**
     * 게시물 목록 조회
     */
    public PostsResultRes readPosts(PostSearchDto postSearchDto){
        Page<PostSimpleDto> pagePosts = null;

        switch (postSearchDto.getPostSort()) {
            case LATESTDESC: // 최신순
                postRepository.findPostsOrderByCreatedDateDesc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case LATESTASC: // 오래된 순
                pagePosts = postRepository.findPostsOrderByCreatedDateAsc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case COMMENTCNTASC: // 댓글 오름차순
                pagePosts = postRepository.findPostsOrderByCommentSizeAsc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case COMMENTCNTDESC: // 댓글 내림차순
                pagePosts = postRepository.findPostsOrderByCommentSizeDesc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case LIKEASC:   // 추천 오름차순
                pagePosts = postRepository.findPostsOrderByLikeAsc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case LIKEDESC:  // 추천 내림차순
                pagePosts = postRepository.findPostsOrderByLikeDesc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case VIEWCNTASC:    // 조회수 오름차순
                pagePosts = postRepository.findPostsOrderByViewAsc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case VIEWCNTDESC:   // 조회수 내림차순
                pagePosts = postRepository.findPostsOrderByViewDesc(postSearchDto, PageRequest.of(postSearchDto.getPage(), MAX_POST_SIZE));
                break;
            case POPULAR:   // 인기순
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

        PostsResultRes postsResultRes = new PostsResultRes(curPageNumber, totalPageCount, next, prev, postSize, postSimpleContent);
        return postsResultRes;
    }

    /**
     * 댓글 하이라이팅
     */
    @Transactional
    public PostDetailWithHighlightCommentRes readPostWithHighlightComment(Long postId, Long commentId, Member member) {

        /** 게시물 정보**/
        Post findPost = getPost(postId);
        Boolean isLikedPost = checkPostLike(findPost.getId(), member);

        /** 댓글 정보 **/
        Comment highlightComment = getComment(commentId);
        List<TopCommentRes> comments = new ArrayList<>();
        Page<CommentWithIsLikeDto> topCommentsPage=null;

        if (highlightComment.getPost()!= findPost) {
            //throw new CustomException() 오류
        }

        int depth = highlightComment.getDepth();

        // 하이라이팅 댓글 기준

        // 최상위 댓글
        if (depth==0) {
            /**
            // 상위 댓글의 페이지 정보를 찾고 해당 페이지의 최상위 댓글 10개를 가져온다.
            topCommentsPage = getTopCommentsPage(member, findPost, highlightComment);
            List<CommentWithIsLikeDto> topCommentsDto = topCommentsPage.getContent();

            // TopCommentRes 생성
            for (CommentWithIsLikeDto tc:topCommentsDto) {
                TopCommentRes topCommentRes = getTopCommentResWithTargetComment2(tc, highlightComment, member, findPost);
                comments.add(topCommentRes);
            }
             **/
        }

        // 깊이 1인 댓글
        else if (depth==1) {
            // 상위 댓글을 찾는다.
            Comment topComment = highlightComment.getParent();

            // 상위 댓글의 페이지 정보를 찾고 해당 페이지의 최상위 댓글 10개를 가져온다.
            topCommentsPage = getTopCommentsPage(member, findPost, topComment);
            List<CommentWithIsLikeDto> topCommentsDto = topCommentsPage.getContent();

            // TopCommentRes 생성
            for (CommentWithIsLikeDto tc:topCommentsDto) {
                TopCommentRes topCommentRes = getTopCommentResWithTargetComment2(tc, highlightComment, member, findPost, 1,null );
                comments.add(topCommentRes);
            }
        }

        // 깊이 2인 댓글
        else {
            // 상위 댓글을 찾는다.
            Comment topComment = highlightComment.getParent().getParent();
            Comment d1Comment = highlightComment.getParent();

            // 상위 댓글의 페이지 정보를 찾고 해당 페이지의 최상위 댓글 10개를 가져온다.
            topCommentsPage = getTopCommentsPage(member, findPost, topComment);
            List<CommentWithIsLikeDto> topCommentsDto = topCommentsPage.getContent();

            // 상위 댓글 별로 하위 댓글 10개씩 조회
            for (CommentWithIsLikeDto tc:topCommentsDto) {
                TopCommentRes topCommentRes = getTopCommentResWithTargetComment2(tc, d1Comment, member, findPost, 2, highlightComment);
                comments.add(topCommentRes);
            }
            
        }
        CommentsRes commentsRes = new CommentsRes(findPost.getId(), comments, topCommentsPage.getNumber(), topCommentsPage.getTotalPages(), topCommentsPage.hasNext(), topCommentsPage.hasPrevious(), topCommentsPage.getSize());

        PostDetailWithHighlightCommentRes postDetailWithHighlightCommentRes = new PostDetailWithHighlightCommentRes(findPost, member, isLikedPost, commentsRes, comments.size(), highlightComment.getId());
        return postDetailWithHighlightCommentRes;
    }

    private TopCommentRes getTopCommentResWithTargetComment2(CommentWithIsLikeDto parentCommentWithIsLikeDto, Comment childComment, Member member, Post post, int depth, Comment d2Comment){

        Comment parentComment = parentCommentWithIsLikeDto.getComment();
        Boolean parentCommentIsLiked = parentCommentWithIsLikeDto.getIsLiked();

        int childCommentPage = 0;
        Long topCommentId = parentComment.getId();
        if (childComment!=null) {
            if (topCommentId == childComment.getParent().getId()) {
                // 해당 댓글의 댓글 페이지를 찾는다. (상위 댓글 기준)
                int childRowNumber = commentRepository.findChildCommentRowNumberByParentCommentId(childComment.getId(), topCommentId);
                childCommentPage = (childRowNumber - 1) / 10;
            }
        }

        // 자식 댓글 페이징 정보 얻음
        Page<CommentWithIsLikeDto> childCommentsPage = commentRepository.findChildCommentWithIsLikeDto(member.getId(), post.getId(), topCommentId, PageRequest.of(childCommentPage, 10));
        List<CommentWithIsLikeDto> childCommentsDto = childCommentsPage.getContent();

        // CommentRes로 변형
        TopCommentRes topCommentRes = new TopCommentRes(parentComment, parentCommentIsLiked, childCommentsPage.getNumber(), childCommentsPage.hasNext(), childCommentsPage.hasPrevious(), childCommentsPage.getTotalPages());
        for (CommentWithIsLikeDto c: childCommentsDto) {

            // 만약 depth가 2라면 해당 댓글의 하위댓글 재귀
            if (depth==2) {
                TopCommentRes tc = getTopCommentResWithTargetComment2(c, d2Comment, member, post, 3, null);
                topCommentRes.addChild(tc);
            }
            else {
                CommentRes cr = new CommentRes(c.getComment(), c.getIsLiked());
                int crChildSize = c.getComment().getChild().size();
                if (crChildSize>0) cr.setHasChild(true);
                topCommentRes.addChild(cr);
            }
        }
        return topCommentRes;
    }

    private Page<CommentWithIsLikeDto> getTopCommentsPage(Member member, Post findPost, Comment topComment) {
        int topCommentRowNumber = commentRepository.findCommentRowNumberByCommentId(topComment.getId());
        int topCommentPage = (topCommentRowNumber-1) / 10;
        log.info("topComment page number = {}", topCommentPage);
        Page<CommentWithIsLikeDto> topCommentsPage = commentRepository.findTopCommentWithIsLikeDto(member.getId(), findPost.getId(), PageRequest.of(topCommentPage, 10));
        return topCommentsPage;
    }

    private Comment getComment(Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));
        return findComment;
    }


//    private List<PostSimpleDto> convertToPostSimpleDetails(List<Post> totalPosts) {
//        List<PostSimpleDto> posts = new ArrayList<>();
//        for (Post post : totalPosts) {
//            Member member = post.getMember();
//            PostSimpleDto postSimpleDetail = new PostSimpleDto(post.getId(), post.getTitle(), member.getId(), member.getName(), member.getProfileImgUrl(), post.getCreatedDate(), post.getViews(), post.getViews());
//            posts.add(postSimpleDetail);
//        }
//        return posts;
//    **/

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
