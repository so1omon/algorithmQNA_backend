package algorithm_QnA_community.algorithm_QnA_community.api.service.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentCreateReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.*;
import algorithm_QnA_community.algorithm_QnA_community.api.service.comment.CommentService;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
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
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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
 * 2023/05/24        janguni       게시물 목록 test 중 존재하지 않은 페이지 번호 테스트
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

    @Autowired
    EntityManager em;

    @BeforeEach
    public void memberSave(){
        Member member = Member.createMember()
                .name("uni2")
                .email("uni12345@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("profile")
                .build();
        memberRepository.save(member);
    }


    @Test
    @Transactional
    void 게시물_등록(){
        // given
        Optional<Member> findMember = memberRepository.findByEmail("uni12345@gmail.com");
        List<String> keyWords= new ArrayList<>();
        keyWords.add("카카오 기출");
        keyWords.add("어려움");
        keyWords.add("반복문");

        PostCreateReq postCreateReq = new PostCreateReq("title", "content", "DP", "QNA", keyWords);

        // when
        postService.writePost(postCreateReq, findMember.get());
        em.flush();
        em.clear();

        // then
        List<Post> posts = findMember.get().getPosts();
        assertThat(posts.size()).isEqualTo(1);
        for (Post post:posts) {
            assertThat(post.getContent()).isEqualTo(postCreateReq.getContent());
            log.info("keyWords={}", post.getKeyWords());
            assertThat(post.getKeyWords()).isEqualTo("카카오 기출,어려움,반복문");
        }
    }

    @Test
    @Transactional
    void 게시물_수정(){
        Optional<Member> findMember = memberRepository.findByEmail("uni12345@gmail.com");

        PostCreateReq postCreateReq = new PostCreateReq("title", "content", "DP", "QNA", null);

        postService.writePost(postCreateReq, findMember.get());

        List<Post> posts = findMember.get().getPosts();

        for (Post post: posts) {
            PostUpdateReq postUpdateReq = new PostUpdateReq("title2", "content2","SORT", "TIP");
            postService.updatePost(post.getId(), postUpdateReq, findMember.get());
        }

        List<Post> posts2 = findMember.get().getPosts();
        for (Post post:posts) {
            assertThat(post.getTitle()).isEqualTo("title2");
            assertThat(post.getContent()).isEqualTo("content2");
            assertThat(post.getPostCategory()).isEqualTo(PostCategory.valueOf("SORT"));
            assertThat(post.getType()).isEqualTo(PostType.valueOf("TIP"));
        }
    }

    @Test
    @Transactional
    void 게시물_삭제(){
        Optional<Member> findMember = memberRepository.findByEmail("uni12345@gmail.com");

        Post post = Post.createPost()
                .member(findMember.get())
                .title("title")
                .content("content")
                .postCategory(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        postService.deletePost(post.getId(), findMember.get());
        em.flush();
        em.clear();


        //Assertions.assertThat(findMember.get().getPosts().size()).isEqualTo(0); // pull후 다시 테스트 할 예정
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
                .postCategory(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);
        List<Post> posts = findMember.get().getPosts();
        for (Post p: posts) {
            Long postId = p.getId();
        }
        Post findPost = postRepository.findById(post.getId()).get();

        LikeReq postLikeReq = new LikeReq(true, false);

        // when
        postService.likePost(post.getId(), postLikeReq, findMember.get());

        // then
        assertThat(findPost.getLikeCnt()).isEqualTo(1);
        assertThat(findPost.getDislikeCnt()).isEqualTo(0);
        assertThat(likePostRepository.findByPostIdAndMemberId(findPost.getId(), findMember.get().getId())).isNotEmpty();
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
                .postCategory(PostCategory.DP)
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
        LikeReq postLikeReq = new LikeReq(false, false);
        postService.likePost(post.getId(), postLikeReq, findMember);

        // then
        assertThat(findPost.getLikeCnt()).isEqualTo(0);
        assertThat(findPost.getDislikeCnt()).isEqualTo(1);
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
                .postCategory(PostCategory.DP)
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
        LikeReq postLikeReq = new LikeReq(true, true);
        postService.likePost(post.getId(), postLikeReq, findMember);

        // then
        assertThat(findPost.getLikeCnt()).isEqualTo(0);
        assertThat(findPost.getDislikeCnt()).isEqualTo(0);
        assertThat(likePostRepository.findByPostIdAndMemberId(findPost.getId(), findMember.getId())).isEmpty();

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
                .postCategory(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        Post findPost = postRepository.findById(post.getId()).get();


        // when
        LikeReq postLikeReq = new LikeReq(true, true);
        postService.likePost(post.getId(), postLikeReq, findMember);

        // then
        assertThat(findPost.getLikeCnt()).isEqualTo(0);
        assertThat(findPost.getDislikeCnt()).isEqualTo(0);
        assertThat(likePostRepository.findByPostIdAndMemberId(findPost.getId(), findMember.getId())).isEmpty();
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
                .postCategory(PostCategory.DP)
                .type(PostType.QNA)
                .build();
        postRepository.save(post);

        // when
        ReportReq postReportReq = new ReportReq("SLANG", null, true);
        postService.reportPost(post.getId(), postReportReq, reportingMember);
        em.flush();
        em.clear();

        // then
        Optional<ReportPost> findReportPost = reportPostRepository.findByPostIdAndMemberId(post.getId(), reportingMember.getId());
        assertThat(findReportPost).isNotEmpty();

        assertThat(findReportPost.get().getDetail()).isEqualTo("기타 사유 없음");
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
                .postCategory(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        // when
        ReportReq postReportReq = new ReportReq("AD",null, true);

        // then
        assertThatThrownBy(() ->postService.reportPost(post.getId(), postReportReq, reportedMember))
                .isInstanceOf(CustomException.class);

    }

    /**
     * 게시물 조회
     */
    @Transactional
    @Test
    void 게시물_조회() {
        // given
        // 게시물 하나 저장, 최상위 댓글 12개, 대댓글 11개, 10개, 9개
        Member findMember = memberRepository.findByEmail("uni12345@gmail.com").get();

        Post post = Post.createPost()
                .member(findMember)
                .title("title")
                .content("content")
                .postCategory(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);
        LikeReq postLikeReq = new LikeReq(true, false);
        postService.likePost(post.getId(), postLikeReq, findMember);

        for (int i=1;i<=12;i++) {
            CommentCreateReq commentCreateReq = new CommentCreateReq("최상위 댓글 내용"+i, null);
            commentService.writeComment(post.getId(), commentCreateReq, findMember);
        }


        int count=0;
        List<Comment> topComments = commentRepository.findTop10ByPostIdAndDepthEqualsOrderByCreatedDateDesc(post.getId(), 0);
        for (Comment c:topComments){
            Long commentId = c.getId();
            if (count==0){
                for(int i=1; i<=11; i++){
                    CommentCreateReq commentCreateReq = new CommentCreateReq(count+"대댓글 내용"+i, commentId);
                    commentService.writeComment(post.getId(), commentCreateReq, findMember);
                }
            }

            else if (count==1){
                for(int i=1; i<=10; i++){
                    CommentCreateReq commentCreateReq = new CommentCreateReq(count+"대댓글 내용"+i, commentId);
                    commentService.writeComment(post.getId(), commentCreateReq, findMember);
                }
            }

            else if (count==2){
                for(int i=1; i<=9; i++){
                    CommentCreateReq commentCreateReq = new CommentCreateReq(count+"대댓글 내용"+i, commentId);
                    commentService.writeComment(post.getId(), commentCreateReq, findMember);
                }
            }
            count+=1;
        }

        //when
        PostDetailRes postDetailRes = postService.readPostDetail(post.getId(), findMember);

        //then
        assertThat(postDetailRes.getPostId()).isEqualTo(post.getId());
        assertThat(postDetailRes.getPostContent()).isEqualTo(post.getContent());
        //log.info(postDetailRes.)

    }

    /**
     * 게시물 목록 조회
     */
    @Transactional
    @Test
    void 게시물_목록_조회() {
        // given  - 게시물 20개
        Member findMember = memberRepository.findByEmail("uni12345@gmail.com").get();
        for (int i = 1; i <= 20; i++) {
            Post post = Post.createPost()
                    .member(findMember)
                    .title("title"+i)
                    .content("content")
                    .postCategory(PostCategory.DP)
                    .type(PostType.TIP)
                    .build();
            postRepository.save(post);
        }

        // when
        log.info("----------------------");
        //PostsResultRes resultRes1 = postService.readPosts(PostCategory.DP, PostType.TIP, PostSortType.LATESTDESC, 0);
        //Assertions.assertThatThrownBy(() -> postService.readPosts(PostCategory.DP, PostType.QNA, PostSortType.LATESTDESC, 2))
                        //.isInstanceOf(CustomException.class);
        log.info("----------------------");

        // then
        //Assertions.assertThat(resultRes1.getCurrentPage()).isEqualTo(0);
        //Assertions.assertThat(resultRes1.getTotalPageCount()).isEqualTo(1);
        //Assertions.assertThat(resultRes1.getSize()).isEqualTo(20);
    }

}