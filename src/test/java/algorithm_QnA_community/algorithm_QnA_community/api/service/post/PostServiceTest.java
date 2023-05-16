package algorithm_QnA_community.algorithm_QnA_community.api.service.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentCreateReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentDetailRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostCreateReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostDetailRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostLikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.service.comment.CommentService;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
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
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.post
 * fileName       : PostServiceTest
 * author         : janguni
 * date           : 2023/05/12
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/12        janguni       최초 생성
 */

@SpringBootTest
@Slf4j
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikePostRepository likePostRepository;

    @Autowired
    private ReportPostRepository reportPostRepository;

    @BeforeEach
    public void memberSave(){
        Member member = Member.createMember()
                .name("uni2")
                .email("uni1234567@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("profile")
                .build();
        memberRepository.save(member);
    }


    @Test
    @Transactional
    void 게시물_등록(){
        Optional<Member> findMember = memberRepository.findByEmail("uni1234@gmail.com");

        PostCreateReq postCreateReq = new PostCreateReq("title", "content", "DP", "QNA");

        postService.writePost(postCreateReq, findMember.get().getId());

        List<Post> posts = findMember.get().getPosts();
        Assertions.assertThat(posts.size()).isEqualTo(1);
        for (Post post:posts) {
            Assertions.assertThat(post.getContent()).isEqualTo(postCreateReq.getContent());
        }
    }

    @Test
    @Transactional
    void 게시물_수정(){
        Optional<Member> findMember = memberRepository.findByEmail("uni1234@gmail.com");

        PostCreateReq postCreateReq = new PostCreateReq("title", "content", "DP", "QNA");

        postService.writePost(postCreateReq, findMember.get().getId());

        List<Post> posts = findMember.get().getPosts();

        for (Post post: posts) {
            PostCreateReq postCreateReq2 = new PostCreateReq("title2", "content2","SORT", "TIP");
            postService.updatePost(post.getId(), postCreateReq2, findMember.get().getId());
        }

        List<Post> posts2 = findMember.get().getPosts();
        for (Post post:posts) {
            Assertions.assertThat(post.getTitle()).isEqualTo("title2");
            Assertions.assertThat(post.getContent()).isEqualTo("content2");
            Assertions.assertThat(post.getCategory()).isEqualTo(PostCategory.valueOf("SORT"));
            Assertions.assertThat(post.getType()).isEqualTo(PostType.valueOf("TIP"));
        }
    }

    @Test
    @Transactional
    void 게시물_삭제(){
        Optional<Member> findMember = memberRepository.findByEmail("uni1234@gmail.com");

        Post post = Post.createPost()
                .member(findMember.get())
                .title("title")
                .content("content")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        postService.deletePost(post.getId(), findMember.get().getId());

        Assertions.assertThat(findMember.get().getPosts().size()).isEqualTo(0); //d여기부터
    }

    /**
     * 게시물 추천 (추천정보가 없을 경우)
     */
    @Test
    @Transactional
    void 게시물_추천_1() {

        // given
        Optional<Member> findMember = memberRepository.findByEmail("uni12345@gmail.com");

        Post post = Post.createPost()
                .member(findMember.get())
                .title("title")
                .content("content")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);
        List<Post> posts = findMember.get().getPosts();
        for (Post p: posts) {
            Long postId = p.getId();
        }
        Post findPost = postRepository.findById(post.getId()).get();

        PostLikeReq postLikeReq = new PostLikeReq(true, false);

        // when
        postService.likePost(post.getId(), postLikeReq, findMember.get().getId());

        // then
        Assertions.assertThat(findPost.getLikeCnt()).isEqualTo(1);
        Assertions.assertThat(findPost.getDislikeCnt()).isEqualTo(0);
        Assertions.assertThat(likePostRepository.findByPostIdAndMemberId(findPost.getId(), findMember.get().getId())).isNotEmpty();


    }

    /**
     * 게시물 추천정보 변경 (추천->비추천 으로 변경)
     */
    @Test
    @Transactional
    void 게시물_추천_2() {

        // given
        Member findMember = memberRepository.findByEmail("uni12345@gmail.com").get();

        Post post = Post.createPost()
                .member(findMember)
                .title("title")
                .content("content")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);
        List<Post> posts = findMember.getPosts();
        for (Post p: posts) {
            Long postId = p.getId();
        }
        Post findPost = postRepository.findById(post.getId()).get();

        LikePost likePost = LikePost.createLikePost()
                .post(findPost)
                .isLike(true)
                .member(findMember)
                .build();

        likePostRepository.save(likePost);

        // when
        PostLikeReq postLikeReq = new PostLikeReq(false, false);
        postService.likePost(post.getId(), postLikeReq, findMember.getId());

        // then
        Assertions.assertThat(findPost.getLikeCnt()).isEqualTo(0);
        Assertions.assertThat(findPost.getDislikeCnt()).isEqualTo(1);
    }

    /**
     * 나의 반응 삭제 (추천정보 삭제)
     */
    @Test
    @Transactional
    void 게시물_추천_3() {

        // given
        Member findMember = memberRepository.findByEmail("uni12345@gmail.com").get();

        Post post = Post.createPost()
                .member(findMember)
                .title("title")
                .content("content")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);
        List<Post> posts = findMember.getPosts();
        for (Post p: posts) {
            Long postId = p.getId();
        }
        Post findPost = postRepository.findById(post.getId()).get();

        LikePost likePost = LikePost.createLikePost()
                .post(findPost)
                .isLike(true)
                .member(findMember)
                .build();

        likePostRepository.save(likePost);

        // when
        PostLikeReq postLikeReq = new PostLikeReq(true, true);
        postService.likePost(post.getId(), postLikeReq, findMember.getId());

        // then
        Assertions.assertThat(findPost.getLikeCnt()).isEqualTo(0);
        Assertions.assertThat(findPost.getDislikeCnt()).isEqualTo(0);
        Assertions.assertThat(likePostRepository.findByPostIdAndMemberId(findPost.getId(), findMember.getId())).isEmpty();

    }

    /**
     * 나의 반응 삭제 (추천정보 삭제)
     *  + 원래 추천정보가 없는 경우
     */
    @Test
    @Transactional
    void 게시물_추천_4() {

        // given
        Member findMember = memberRepository.findByEmail("uni12345@gmail.com").get();

        Post post = Post.createPost()
                .member(findMember)
                .title("title")
                .content("content")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        Post findPost = postRepository.findById(post.getId()).get();


        // when
        PostLikeReq postLikeReq = new PostLikeReq(true, true);
        postService.likePost(post.getId(), postLikeReq, findMember.getId());

        // then
        Assertions.assertThat(findPost.getLikeCnt()).isEqualTo(0);
        Assertions.assertThat(findPost.getDislikeCnt()).isEqualTo(0);
        Assertions.assertThat(likePostRepository.findByPostIdAndMemberId(findPost.getId(), findMember.getId())).isEmpty();

    }

    /**
     * (정상)
     * 게시물 신고
     */
    @Test
    @Transactional
    void 게시물_신고() {

        // given
        Member reportedMember = memberRepository.findByEmail("uni12345@gmail.com").get();

        Member reportingMember = Member.createMember()
                .name("uni3")
                .email("uni123456@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("profile")
                .build();
        memberRepository.save(reportingMember);

        Post post = Post.createPost()
                .member(reportedMember)
                .title("title")
                .content("content")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        Post reportedPost = postRepository.findById(post.getId()).get();


        // when
        PostReportReq postReportReq = new PostReportReq("AD",null);
        postService.reportPost(post.getId(), postReportReq, reportingMember.getId());

        // then
        Optional<ReportPost> findReportPost = reportPostRepository.findByPostIdAndMemberId(post.getId(), reportingMember.getId());
        Assertions.assertThat(findReportPost).isNotEmpty();
        Assertions.assertThat(findReportPost.get().getPost()).isEqualTo(post);
        Assertions.assertThat(findReportPost.get().getMember()).isEqualTo(reportingMember);
        Assertions.assertThat(findReportPost.get().getCategory()).isEqualTo(ReportCategory.AD);
        Assertions.assertThat(findReportPost.get().getDetail()).isEqualTo("기타 사유 없음"); //여기부터

        List<ReportPost> reportPosts = reportedPost.getReportPosts();
        for (ReportPost rp: reportPosts) {
            Assertions.assertThat(rp.getPost()).isEqualTo(post);
            Assertions.assertThat(rp.getMember()).isEqualTo(reportingMember);
            Assertions.assertThat(rp.getCategory()).isEqualTo(ReportCategory.AD);
            Assertions.assertThat(rp.getDetail()).isEqualTo("기타 사유 없음");
        }


    }

    /**
     * (오류) - 자신이 작성한 게시물을 신고한 경우
     * 게시물 신고
     */
    @Transactional
    @Test
    void 게시물_신고2() {

        // given
        Member reportedMember = memberRepository.findByEmail("uni12345@gmail.com").get();

        Post post = Post.createPost()
                .member(reportedMember)
                .title("title")
                .content("content")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        Post reportedPost = postRepository.findById(post.getId()).get();

        // when
        PostReportReq postReportReq = new PostReportReq("AD",null);

        // then
//        Assertions.assertThatThrownBy(() -> postService.reportPost(post.getId(), postReportReq, reportedMember.getId()))
//                .isInstanceOf(CustomException.class);
        // then
        postService.reportPost(post.getId(), postReportReq, reportedMember.getId());

    }

    /**
     * 게시물 조회
     */
    @Transactional
    @Test
    void 게시물_조회() {
        // given
        // 게시물 하나 저장, 최상위 댓글 12개, 대댓글 11개, 10개, 9개
        Member findMember = memberRepository.findByEmail("uni1234567@gmail.com").get();

        Post post = Post.createPost()
                .member(findMember)
                .title("title")
                .content("content")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);
        PostLikeReq postLikeReq = new PostLikeReq(true, false);
        postService.likePost(post.getId(), postLikeReq, findMember.getId());

        for (int i=1;i<=12;i++) {
            CommentCreateReq commentCreateReq = new CommentCreateReq("최상위 댓글 내용"+i, null);
            commentService.writeComment(post.getId(), commentCreateReq, findMember.getId());
        }


        int count=0;
        //List<Comment> comments = commentRepository.findByPostId(post.getId());
        //List<Comment> topComments = commentRepository.findTop10ByParentIdAndDepthEqualsOrderByCreatedDateDesc(post.getId(), 0);
        List<Comment> topComments = commentRepository.findTop10ByPostIdAndDepthEqualsOrderByCreatedDateDesc(post.getId(), 0);
        for (Comment c:topComments){
            Long commentId = c.getId();
            if (count==0){
                for(int i=1; i<=11; i++){
                    CommentCreateReq commentCreateReq = new CommentCreateReq(count+"대댓글 내용"+i, commentId);
                    commentService.writeComment(post.getId(), commentCreateReq, findMember.getId());
                }
            }

            else if (count==1){
                for(int i=1; i<=10; i++){
                    CommentCreateReq commentCreateReq = new CommentCreateReq(count+"대댓글 내용"+i, commentId);
                    commentService.writeComment(post.getId(), commentCreateReq, findMember.getId());
                }
            }

            else if (count==2){
                for(int i=1; i<=9; i++){
                    CommentCreateReq commentCreateReq = new CommentCreateReq(count+"대댓글 내용"+i, commentId);
                    commentService.writeComment(post.getId(), commentCreateReq, findMember.getId());
                }
            }
            count+=1;
        }

        //when
        PostDetailRes postDetailRes = postService.readPost(post.getId(), findMember.getId());

        //then
        Assertions.assertThat(postDetailRes.getPostId()).isEqualTo(post.getId());
        Assertions.assertThat(postDetailRes.getContent()).isEqualTo(post.getContent());
        Assertions.assertThat(postDetailRes.getCommentTotalCount()).isEqualTo(42);
        Assertions.assertThat(postDetailRes.getCommentSize()).isEqualTo(39);
        Assertions.assertThat(postDetailRes.getMemberId()).isEqualTo(findMember.getId());
        List<CommentDetailRes> resComments = postDetailRes.getComments();
    }

}