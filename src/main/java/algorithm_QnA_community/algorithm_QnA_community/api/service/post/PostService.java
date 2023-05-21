package algorithm_QnA_community.algorithm_QnA_community.api.service.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentDetailRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentsRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.*;
import algorithm_QnA_community.algorithm_QnA_community.api.service.comment.CommentService;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikeComment;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 *                                                  추천/비추천(댓글) 정보 코드 수정*/

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    private final LikePostRepository likePostRepository;

    private final LikeCommentRepository likeCommentRepository;

    private final ReportPostRepository reportPostRepository;

    private final CommentRepository commentRepository;

    private final CommentService commentService;


    /**
     * 게시물 등록
     */
    @Transactional
    public PostWriteRes writePost(PostCreateReq postCreateReq, Member member){

        // 일반 사용자가 공지사항 타입을 선택한 경우
        checkNoticePermission(member.getRole(), postCreateReq.getPostType());

        Post post = Post.createPost()
                .member(member)
                .title(postCreateReq.getTitle())
                .content(postCreateReq.getContent())
                .category(PostCategory.valueOf(postCreateReq.getPostCategory()))
                .type(PostType.valueOf(postCreateReq.getPostType()))
                .build();

        Post savedPost = postRepository.save(post);

        return new PostWriteRes(savedPost.getId(), savedPost.getCreatedDate());
    }

    /**
     * 게시물 수정
     */
    @Transactional
    public void updatePost(Long postId,PostUpdateReq postUpdateReq, Member member) {

        // 일반 사용자가 공지사항 타입을 선택한 경우
        checkNoticePermission(member.getRole(), postUpdateReq.getPostType());

        Post findPost = getPostById(postId);

        // 본인이 쓴 게시물이 맞는지 확인
        checkPostAccessPermission(member != findPost.getMember(), ErrorCode.UNAUTHORIZED, "게시물을 삭제할 권한이 없습니다.");

        setIfNotNull(postUpdateReq.getTitle(), findPost::updateTitle);
        setIfNotNull(postUpdateReq.getContent(), findPost::updateContent);
        setIfNotNull(PostCategory.valueOf(postUpdateReq.getPostCategory()), findPost::updateCategory);
        setIfNotNull(PostType.valueOf(postUpdateReq.getPostType()),findPost::updateType);
    }

    /**
     * 게시물 삭제
     */
    @Transactional
    public void deletePost(Long postId, Member member) {
        Post findPost = getPostById(postId);

        // 본인이 쓴 게시물이 맞는지 확인
        checkPostAccessPermission(member != findPost.getMember(), ErrorCode.UNAUTHORIZED, "게시물을 삭제할 권한이 없습니다.");

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

        Post findPost = getPostById(postId);

        Optional<LikePost> findLikePost = likePostRepository.findByPostIdAndMemberId(postId, member.getId());

        if (postLikeReq.getCancel()){
            if (!findLikePost.isPresent()) log.info("추천정보가 존재하지 않음");
            else {
                findPost.updateLikeCnt(postLikeReq.getIsLike(), false);
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

        Post findPost = getPostById(postId);

        // 본인 게시물을 신고하려는 경우
        checkPostAccessPermission(member == findPost.getMember(), ErrorCode.REPORT_MY_RESOURCE, "자신이 작성한 게시물은 신고할 수 없습니다.");


        if (postReportReq.getCategory().equals(ReportCategory.ETC.toString()) & postReportReq.getDetail()==null){

        }

        Optional<ReportPost> findReportPost = reportPostRepository.findByPostIdAndMemberId(postId, member.getId());

        if (!findReportPost.isPresent()){ // 해당 게시물을 신고한 적이 없다면
            ReportPost reportPost = ReportPost.createReportPost()
                    .post(findPost)
                    .member(member)
                    .category(ReportCategory.valueOf(postReportReq.getCategory()))
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
    public PostDetailRes readPostDetail(Long postId, Member member){

        //**** 게시물 정보 ****//
        Post findPost = getPostById(postId); // 게시물
        Boolean isLikedPost = checkPostLike(postId, member); // 게시물 추천 정보



        //**** 작성자 정보 ****//
        Member postingMember = memberRepository.findById(findPost.getMember().getId()).get();



        //**** 댓글 정보 ****//
        CommentsRes commentsRes = commentService.getComments(postId, 0, member.getId());


        // depth=0, 1인 댓글 불러오기
//        List<Comment> resultComments = new ArrayList<>();
//        List<Comment> topComments = commentRepository.findTop10ByPostIdAndDepthEqualsOrderByCreatedDateDesc(findPost.getId(), 0);
//        int topCommentSize = topComments.size();
//        for (Comment tc: topComments) {
//            resultComments.add(tc);
//            List<Comment> depth1Comments = commentRepository.findTop10ByParentIdAndDepthEqualsOrderByCreatedDateDesc(tc.getId(), 1);
//            for (Comment d1c: depth1Comments) {
//                resultComments.add(d1c);
//            }
//        }
//
//        // 불러온 댓글 response 형태로 커스텀
//        List<CommentRes> commentsDto = new ArrayList<>();
//        for (Comment rc:resultComments){
//
//            // 사용자의 해당 댓글의 추천정보
//            Optional<LikeComment> likeComment = likeCommentRepository.findByCommentIdAndMemberId(rc.getId(), member.getId());
//            Boolean isLikedComment = checkCommentLike(likeComment);
//
//            CommentRes commentRes = new CommentRes(rc, isLikedComment);
//            commentsDto.add(commentRes);
//        }
//
//        // 총 댓글 갯수
            int totalCommentSize = commentRepository.countCommentByPostId(postId);
//
//        // 댓글 다음 페이지 여부
//        boolean commentNextPage = topComments.size()>10;
//
//        // 댓글 총 페이지 갯수
//        int totalPageSize = getTotalPageSize(totalCommentSize);

        //**** Response 객체 생성 ****//
        PostDetailRes postDetailRes =
            new PostDetailRes(findPost, member, isLikedPost, commentsRes, totalCommentSize);
        return postDetailRes;
    }

    private int getTotalPageSize(int totalCommentSize) {
        int totalPageSize = totalCommentSize / 10;
        if (totalPageSize % 10 !=0) {
            totalPageSize +=1;
        }
        return totalPageSize;
    }

    private Boolean checkCommentLike(Optional<LikeComment> likeComment) {
        Boolean isLikedComment;
        if (likeComment.isPresent()){
            isLikedComment = likeComment.get().isLike();
        } else {
            isLikedComment = null;
        }
        return isLikedComment;
    }

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
    public PostsResultRes readPosts(PostCategory categoryName, PostType postType,PostSortType sortName, int pageNumber){
        List<Post> totalPosts=null;

        switch (sortName) {
            case LATESTDESC: // 최신순
                totalPosts = postRepository.findByCategoryAndTypeOrderByCreatedDateDesc(categoryName, postType);
                break;
            case LATESTASC: // 오래된 순
                totalPosts = postRepository.findByCategoryAndTypeOrderByCreatedDateAsc(categoryName, postType);
                break;
            case COMMENTCNTASC: // 댓글 오름차순
                totalPosts = postRepository.findPostOrderByCommentCntAsc(categoryName);
                break;
            case COMMENTCNTDESC: // 댓글 내림차순
                totalPosts = postRepository.findPostOrderByCommentCntDesc(categoryName);
                break;
            case LIKEASC:   // 추천 오름차순
                totalPosts = postRepository.findByCategoryOrderByLike_DislikeASC(categoryName);
                break;
            case LIKEDESC:  // 추천 내림차순
                totalPosts = postRepository.findByCategoryOrderByLike_DislikeDESC(categoryName);
                break;
            case VIEWCNTASC:    // 조회수 오름차순
                totalPosts = postRepository.findByCategoryOrderByViewsAsc(categoryName);
                break;
            case VIEWCNTDESC:   // 조회수 내림차순
                totalPosts = postRepository.findByCategoryOrderByViewsDesc(categoryName);
                break;
            case POPULAR:   // 인기순
                totalPosts = postRepository.findByPostOrderByPopular(categoryName.toString());
                break;
        }

        // 총 페이지 수
        int postsSize = totalPosts.size();
        int totalPageCount = postsSize/20;
        if (postsSize % 20 != 0) {
            totalPageCount += 1;
        }

        // 존재하는 페이지인지 확인
        checkPostAccessPermission(totalPageCount < pageNumber || pageNumber <= 0, ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않은 페이지 번호 입니다.");

        // 전 페이지, 후 페이지 유무
        boolean prev = (pageNumber==1) ? false : true;
        boolean next = (pageNumber==totalPageCount) ? false : true;


        int startPostIdx = (pageNumber-1) * 20;
        int lastPostIdx;
        if (postsSize<=(startPostIdx + 19)){
            lastPostIdx = postsSize-1;
        } else {
            lastPostIdx = startPostIdx + 19;
        }

        List<PostSimpleDetail> posts = new ArrayList<>();

        for (int i = startPostIdx; i <= lastPostIdx; i++) {
            Post post= totalPosts.get(i);
            Member member = post.getMember();
            PostSimpleDetail postSimpleDetail = new PostSimpleDetail(post.getId(), post.getTitle(), member.getId(), member.getName(), member.getProfileImgUrl(), post.getCreatedDate(), post.getViews(), post.getComments().size());
            posts.add(postSimpleDetail);
        }

        PostsResultRes postsResultRes = new PostsResultRes(pageNumber, totalPageCount, next, prev, lastPostIdx-startPostIdx+1, posts);
        return postsResultRes;
    }

    private Post getPostById(Long postId) {
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
