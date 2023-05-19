package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.service.post.PostService;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : PostRepositoryTest
 * author         : solmin
 * date           : 2023/05/01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/01        solmin       최초 생성
 * 2023/05/15        janguni      게시물_정렬_댓글순(), 게시물_정렬_인기순() 추가
 */
@SpringBootTest
@Transactional
@Slf4j
class PostRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostService postService;

    @Autowired
    EntityManager em;

    @Test
    public void 게시글_생성_테스트() {
        Member member = Member.createMember()
            .name("solmin")
            .email("solmin3665@gmail.com")
            .role(Role.ROLE_USER)
            .profileImgUrl("awefawefa")
            .build();
        memberRepository.save(member);

        for (int i = 0; i < 4; i++) {
            postRepository.save(Post.createPost()
                .title("게시글" + i)
                .category(PostCategory.DFS_BFS)
                .content("<p>bfs어려워요" + i + "</p")
                .member(member)
                .build()
            );
        }


        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        List<Post> posts = findMember.getPosts();

        for (Post post1 : posts) {
            System.out.println("post1.title " + post1.getTitle());
        }
    }

    @Test
    @Transactional
    public void 게시물_삭제() {
        Member member = Member.createMember()
                .name("solmin")
                .email("solmin3665@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member);


        Post post = Post.createPost()
                .title("게시글")
                .category(PostCategory.DFS_BFS)
                .content("<p>bfs어려워요</p")
                .member(member)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        Post findPost = postRepository.findById(post.getId()).get();
        log.info("=====");
        postRepository.deleteById(findPost.getId());
        log.info("=====");


        Member findMember = memberRepository.findById(member.getId()).get();
        Assertions.assertThat(findMember.getPosts().size()).isEqualTo(0);


    }

    @Test
    public void 게시물_정렬_댓글순(){
        Member member = Member.createMember()
                .name("solmin")
                .email("solmin3665@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member);

        for (int i = 0; i < 4; i++) {
            Post post = Post.createPost()
                    .member(member)
                    .title("게시물 제목")
                    .content("게시물 내용")
                    .category(PostCategory.DP)
                    .type(PostType.QNA)
                    .build();
            postRepository.save(post);

            if (i==3) {
                Comment comment2 = Comment.createComment()
                        .member(member)
                        .post(post)
                        .content("댓글")
                        .parent(null)
                        .build();
                post.updateContent("댓글 일등");
                commentRepository.save(comment2);
            }

            else if (i==1) {
                post.updateContent("댓글 꼴등");
                continue;
            }

            Comment comment = Comment.createComment()
                    .member(member)
                    .post(post)
                    .content("댓글")
                    .parent(null)
                    .build();
            commentRepository.save(comment);

        }

        // 댓글 갯수 내림차순 test
        List<Post> posts = postRepository.findPostOrderByCommentCntDesc(PostCategory.DP);
        Post post = posts.get(0);
        Assertions.assertThat(post.getContent()).isEqualTo("댓글 일등");

        // 댓글 갯수 오름차순 test
        List<Post> posts2 = postRepository.findPostOrderByCommentCntAsc(PostCategory.DP);
        Post post2 = posts2.get(0);
        Assertions.assertThat(post2.getContent()).isEqualTo("댓글 꼴등");
    }

    @Test
    void 게시물_정렬_인기순(){

        //given (게시물 3개)
        Member member1 = Member.createMember()
                .name("m1")
                .email("m1@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member1);

        Member member2 = Member.createMember()
                .name("m2")
                .email("m2@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member2);
        Member member3 = Member.createMember()
                .name("m3")
                .email("m3@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member3);

        Member member4 = Member.createMember()
                .name("m4")
                .email("m4@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member4);
        Member member5 = Member.createMember()
                .name("m5")
                .email("m5@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member5);
        Member member6 = Member.createMember()
                .name("m6")
                .email("m6@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member6);
        Member member7 = Member.createMember()
                .name("m7")
                .email("m7@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member7);
        Member member8 = Member.createMember()
                .name("m8")
                .email("m8@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member8);
        Member member9 = Member.createMember()
                .name("m9")
                .email("m9@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member9);
        Member member10 = Member.createMember()
                .name("m10")
                .email("m10@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member10);
        Member member11 = Member.createMember()
                .name("m11")
                .email("m11@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member11);
        Member member12 = Member.createMember()
                .name("m12")
                .email("m12@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("awefawefa")
                .build();
        memberRepository.save(member12);



        // 게시물 2
        // view = 5, 추천수 = 3, 비추천수 = 1, 댓글 = 1  => 2.5 + 0.675 + 0 = 3.375
        Post post2 = Post.createPost()
                .member(member1)
                .title("게시물2")
                .content("게시물 내용")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();
        postRepository.save(post2);

        for (int i = 0; i < 5 ; i++) {
            post2.updateViews();
        }

        LikeReq postLikeReq3 = new LikeReq(true, false);
        postService.likePost(post2.getId(), postLikeReq3, member1);
        postService.likePost(post2.getId(), postLikeReq3, member2);
        postService.likePost(post2.getId(), postLikeReq3, member3);
        LikeReq postLikeReq4 = new LikeReq(false, false);
        postService.likePost(post2.getId(), postLikeReq4, member4);

        Comment comment = Comment.createComment()
                .member(member1)
                .post(post2)
                .content("댓글")
                .parent(null)
                .build();
        commentRepository.save(comment);

        // 게시물 1
        // view = 5, 추천수 = 3, 비추천수 = 1, 댓글 = 0  => 2.5 + 0.675 + 0 = 3.175
        Post post1 = Post.createPost()
                .member(member1)
                .title("게시물1")
                .content("게시물 내용")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();
        postRepository.save(post1);

        for (int i = 0; i < 5 ; i++) {
            post1.updateViews();
        }

        LikeReq postLikeReq1 = new LikeReq(true, false);
        postService.likePost(post1.getId(), postLikeReq1, member1);
        postService.likePost(post1.getId(), postLikeReq1, member2);
        postService.likePost(post1.getId(), postLikeReq1, member3);
        LikeReq postLikeReq2 = new LikeReq(false, false);
        postService.likePost(post1.getId(), postLikeReq2, member4);

        // 게시물 3
        // view = 11, 추천수 = 10, 비추천수 = 1, 댓글 = 0  => 5.5 + 2.7272 + 2.7272 + 0 = 8.2272
        Post post3 = Post.createPost()
                .member(member1)
                .title("게시물3")
                .content("게시물 내용")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();
        postRepository.save(post3);

        for (int i = 0; i < 11 ; i++) {
            post3.updateViews();
        }

        LikeReq postLikeReq5 = new LikeReq(true, false);
        postService.likePost(post3.getId(), postLikeReq5, member1);
        postService.likePost(post3.getId(), postLikeReq5, member2);
        postService.likePost(post3.getId(), postLikeReq5, member3);
        postService.likePost(post3.getId(), postLikeReq5, member4);
        postService.likePost(post3.getId(), postLikeReq5, member5);
        postService.likePost(post3.getId(), postLikeReq5, member6);
        postService.likePost(post3.getId(), postLikeReq5, member7);
        postService.likePost(post3.getId(), postLikeReq5, member8);
        postService.likePost(post3.getId(), postLikeReq5, member9);
        postService.likePost(post3.getId(), postLikeReq5, member10);
        LikeReq postLikeReq6 = new LikeReq(false, false);
        postService.likePost(post3.getId(), postLikeReq6, member11);

        Comment comment2 = Comment.createComment()
                .member(member1)
                .post(post3)
                .content("댓글")
                .parent(null)
                .build();
        commentRepository.save(comment2);

        // when
        List<Post> posts = postRepository.findByPostOrderByPopular("DP");

        // then
        Assertions.assertThat(posts.get(0)).isEqualTo(post3);
        Assertions.assertThat(posts.get(1)).isEqualTo(post2);
        Assertions.assertThat(posts.get(2)).isEqualTo(post1);

    }
}