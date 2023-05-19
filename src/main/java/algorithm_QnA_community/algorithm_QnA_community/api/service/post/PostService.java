package algorithm_QnA_community.algorithm_QnA_community.api.service.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentDetailRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.*;
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
 * 2023/05/19        janguni           중복 코드 checkNoticePermission(), getPostById(), checkPostAccessPermission()로 추출
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    private final LikePostRepository likePostRepository;

    private final ReportPostRepository reportPostRepository;

    private final CommentRepository commentRepository;


    /**
     * 게시물 등록
     */
    @Transactional
    public void writePost(PostCreateReq postCreateReq, Member member){

        // 일반 사용자가 공지사항 타입을 선택한 경우
        checkNoticePermission(member.getRole(), postCreateReq.getContentType());

        Post post = Post.createPost()
                .member(member)
                .title(postCreateReq.getTitle())
                .content(postCreateReq.getContent())
                .category(PostCategory.valueOf(postCreateReq.getCategoryName()))
                .type(PostType.valueOf(postCreateReq.getContentType()))
                .build();

        postRepository.save(post);
    }

    /**
     * 게시물 수정
     */
    @Transactional
    public void updatePost(Long postId,PostUpdateReq postUpdateReq, Member member) {

        // 일반 사용자가 공지사항 타입을 선택한 경우
        checkNoticePermission(member.getRole(), postUpdateReq.getContentType());

        Post findPost = getPostById(postId);

        // 본인이 쓴 게시물이 맞는지 확인
        checkPostAccessPermission(member != findPost.getMember(), ErrorCode.UNAUTHORIZED, "게시물을 삭제할 권한이 없습니다.");

        setIfNotNull(postUpdateReq.getTitle(), findPost::updateTitle);
        setIfNotNull(postUpdateReq.getContent(), findPost::updateContent);
        setIfNotNull(PostCategory.valueOf(postUpdateReq.getCategoryName()), findPost::updateCategory);
        setIfNotNull(PostType.valueOf(postUpdateReq.getContentType()),findPost::updateType);
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

        Post findPost = getPostById(postId);

        // 게시물 작성자
        Member postingMember = memberRepository.findById(findPost.getMember().getId()).get();

        // depth=0, 1인 댓글 불러오기
        List<Comment> resultComments = new ArrayList<>();

        List<Comment> topComments = commentRepository.findTop10ByPostIdAndDepthEqualsOrderByCreatedDateDesc(findPost.getId(), 0);
        for (Comment tc: topComments) {
            resultComments.add(tc);
            List<Comment> depth1Comments = commentRepository.findTop10ByParentIdAndDepthEqualsOrderByCreatedDateDesc(tc.getId(), 1);
            for (Comment d1c: depth1Comments) {
                resultComments.add(d1c);
            }
        }

        List<CommentDetailRes> responseComments = new ArrayList<>();

        for (Comment rc:resultComments){
            // 해당 사용자가 추천을 눌렀는지
            Optional<LikePost> findLikePost = likePostRepository.findByPostIdAndMemberId(postId, member.getId());
            boolean isLiked = (findLikePost.isPresent()) ? true : false;

            // 부모댓글 존재 여부
            Long parentId = (rc.getDepth()==0) ? null : rc.getParent().getId();

            CommentDetailRes commentDetailRes = new CommentDetailRes(rc.getId(), parentId,
                    postingMember.getId(), postingMember.getName(), postingMember.getProfileImgUrl(),
                    postingMember.getCommentBadgeCnt() / 10, postingMember.getPostBadgeCnt() / 10, postingMember.getLikeBadgeCnt() / 10,
                    rc.getContent(), rc.getLikeCnt(), rc.getDislikeCnt(), rc.getCreatedDate(),
                    rc.getDepth(),rc.isPinned(),isLiked);
            responseComments.add(commentDetailRes);
        }

        boolean commentNextPage = (topComments.size()>10) ? true : false;
        int totalCommentSize = commentRepository.findByPostId(postId).size();
        PostDetailRes postDetailRes = new PostDetailRes(postId, postingMember.getId(), postingMember.getName(),
                postingMember.getCommentBadgeCnt(), postingMember.getPostBadgeCnt(), postingMember.getLikeBadgeCnt(),
                findPost.getTitle(), findPost.getContent(), findPost.getCreatedDate(),
                findPost.getLikeCnt(), findPost.getDislikeCnt(), totalCommentSize,
                0, commentNextPage, false, responseComments.size(), responseComments);

        return postDetailRes;
    }

    public PostsResultRes readPosts(PostCategory categoryName, PostSortType sortName, int pageNumber){
        List<Post> totalPosts=null;

        switch (sortName) {
            case LATESTDESC: // 최신순
                totalPosts = postRepository.findByCategoryOrderByCreatedDateDesc(categoryName);
                break;
            case LATESTASC: // 오래된 순
                totalPosts = postRepository.findByCategoryOrderByCreatedDateAsc(categoryName);
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
