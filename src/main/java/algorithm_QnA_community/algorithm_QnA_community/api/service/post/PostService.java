package algorithm_QnA_community.algorithm_QnA_community.api.service.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentsRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.*;
import algorithm_QnA_community.algorithm_QnA_community.api.service.comment.CommentService;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
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
 *                                                  추천/비추천(댓글) 정보 코드 수정
 * 2023/05/24        janguni            게시물 조회 시 해당 게시물 조회수+1 처리
 * 2023/05/26        solmin             좋아요 정보 삭제 시 연관관계 끊는 메소드 수행
 *                                      (원래 안건드릴려고 했는데 likeComment랑 너무 겹치는 내용이라 수정했어요 ㅜㅜ)
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
                .postCategory(PostCategory.valueOf(postCreateReq.getPostCategory()))
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
        Post findPost = getPostById(postId); // 게시물
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
     * 게시물 목록 조회
     */
    public PostsResultRes readPosts(PostCategory categoryName, PostType postType, PostSortType sortName, int pageNumber){
        Page<Post> pagePosts=null;

        switch (sortName) {
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

        // 게시물 데이터
        List<Post> posts = pagePosts.getContent();
        int postSize = posts.size();

        if (postSize==0) { // 존재하지 않은 페이지 번호 일 경우
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않은 페이지 번호 입니다.");
        }

        // 페이징 정보
        int totalPageCount = pagePosts.getTotalPages(); // 전체 페이지 수
        boolean prev = pagePosts.hasPrevious(); // 전 페이지 유무
        boolean next = pagePosts.hasNext(); // 후 페이지 유무
        int curPageNumber = pagePosts.getNumber();


        // Post -> PostSimpleDetail
        List<PostSimpleDetail> postDetail = convertToPostSimpleDetails(posts);

        PostsResultRes postsResultRes = new PostsResultRes(curPageNumber, totalPageCount, next, prev, postSize, postDetail);
        return postsResultRes;
    }

    private List<PostSimpleDetail> convertToPostSimpleDetails(List<Post> totalPosts) {
        List<PostSimpleDetail> posts = new ArrayList<>();
        for (Post post : totalPosts) {
            Member member = post.getMember();
            PostSimpleDetail postSimpleDetail = new PostSimpleDetail(post.getId(), post.getTitle(), member.getId(), member.getName(), member.getProfileImgUrl(), post.getCreatedDate(), post.getViews(), post.getComments().size());
            posts.add(postSimpleDetail);
        }
        return posts;
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
