package algorithm_QnA_community.algorithm_QnA_community.api.service.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostCreateReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostLikeReq;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikePost;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import algorithm_QnA_community.algorithm_QnA_community.repository.LikePostRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
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
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikePostRepository likePostRepository;

    @BeforeEach
    public void membersave(){
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
        Res res = postService.likePost(post.getId(), postLikeReq, findMember.get().getId());

        // then
        Assertions.assertThat(res.getStatus().getCode()).isEqualTo(200);
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
        Res res = postService.likePost(post.getId(), postLikeReq, findMember.getId());

        // then
        Assertions.assertThat(res.getStatus().getCode()).isEqualTo(200);
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
        Res res = postService.likePost(post.getId(), postLikeReq, findMember.getId());

        // then
        Assertions.assertThat(res.getStatus().getCode()).isEqualTo(200);
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
        Res res = postService.likePost(post.getId(), postLikeReq, findMember.getId());

        // then
        Assertions.assertThat(res.getStatus().getCode()).isEqualTo(200);
        Assertions.assertThat(findPost.getLikeCnt()).isEqualTo(0);
        Assertions.assertThat(findPost.getDislikeCnt()).isEqualTo(0);
        Assertions.assertThat(likePostRepository.findByPostIdAndMemberId(findPost.getId(), findMember.getId())).isEmpty();

    }

}