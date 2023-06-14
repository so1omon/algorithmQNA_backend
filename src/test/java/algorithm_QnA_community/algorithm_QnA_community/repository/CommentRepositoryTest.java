package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentWithIsLikeDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikeComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : CommentRepositoryTest
 * author         : solmin
 * date           : 2023/05/04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/04        solmin       최초 생성
 * 2023/05/15        janguni      댓글 조회 테스트 추가
 * 2023/06/12        janguni      댓글_조회_추천정보포함 추가
 */

@SpringBootTest
@Transactional
//@Rollback
@Slf4j
class CommentRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    LikeCommentRepository likeCommentRepository;



    @Autowired
    EntityManager em;

    void clear(){
        em.flush();
        em.clear();
    }
    @Test
    @Transactional
    public void 댓글_생성_테스트() {
        Member member = memberRepository.findByName("solmin").get(0);
        Post post = member.getPosts().get(0);
        Comment comment = Comment.createComment()
            .member(member)
            .post(post)
            .content("<p>awefaewfawef</p>")
            .parent(null)
            .build();
        commentRepository.save(comment);
        clear();

        Member findMember = memberRepository.findByName("solmin").get(0);
        Comment comment1 = findMember.getComments().get(0);
    }


    //@BeforeTestExecution
    @BeforeEach
    void getMember() {
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
                .postCategory(PostCategory.DFS_BFS)
                .content("<p>bfs어려워요" + i + "</p")
                            .type(PostType.QNA)
                .member(member)
                .build()
            );
        }


        em.flush();
        em.clear();
    }
    @Test
    @Transactional
    public void 댓글_조회_테스트(){
        // 멤버 생성
        Member member = Member.createMember()
                .name("uni3")
                .email("uni123456@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("profile")
                .build();
        memberRepository.save(member);


        // 게시물 생성
        Post post = Post.createPost()
                .member(member)
                .title("title")
                .content("content")
                .postCategory(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        // 댓글 생성
        Comment comment = Comment.createComment()
                .member(member)
                .post(post)
                .content("<p>댓글</p>")
                .parent(null)
                .build();
        commentRepository.save(comment);

        Comment comment7 = Comment.createComment()
                .member(member)
                .post(post)
                .content("<p>댓글7</p>")
                .parent(null)
                .build();
        commentRepository.save(comment7);

        Comment comment5 = Comment.createComment()
                .member(member)
                .post(post)
                .content("<p>댓글5</p>")
                .parent(null)
                .build();
        commentRepository.save(comment5);

        Comment comment6 = Comment.createComment()
                .member(member)
                .post(post)
                .content("<p>댓글6</p>")
                .parent(null)
                .build();
        commentRepository.save(comment6);

        Comment comment2 = Comment.createComment()
                .member(member)
                .post(post)
                .content("<p>대댓글1</p>")
                .parent(comment)
                .build();
        commentRepository.save(comment2);

        Comment comment3 = Comment.createComment()
                .member(member)
                .post(post)
                .content("<p>대대댓글1</p>")
                .parent(comment2)
                .build();
        commentRepository.save(comment3);

        Comment comment4 = Comment.createComment()
                .member(member)
                .post(post)
                .content("<p>대댓글2</p>")
                .parent(comment)
                .build();
        commentRepository.save(comment4);

        List<Comment> comments = commentRepository.findTop10ByPostIdAndDepthEqualsOrderByCreatedDateDesc(post.getId(),0);


        for (Comment c2: comments) {
            log.info("최상단 comment.content={}", c2.getContent());

            List<Comment> childComments = commentRepository.findTop10ByParentIdAndDepthEqualsOrderByCreatedDateDesc(c2.getId(), 1);

            for (Comment c3: childComments) {
                log.info("          대댓글 comment.content={}", c3.getContent());
            }
        }

        int totalCommentCount = commentRepository.countCommentByPostId(post.getId());
        Assertions.assertThat(totalCommentCount).isEqualTo(7);

    }

    @Test
    @Transactional
    public void 댓글_조회_추천정보포함() {

        // given
        // 멤버 생성
        Member member = Member.createMember()
                .name("uni3")
                .email("uni123456@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("profile")
                .build();
        memberRepository.save(member);


        // 게시물 생성
        Post post = Post.createPost()
                .member(member)
                .title("title")
                .content("content")
                .postCategory(PostCategory.DP)
                .type(PostType.QNA)
                .build();

        postRepository.save(post);

        Long parentId=1L;

        // 댓글 생성
        for (int i = 0; i < 8; i++) {
            Comment parentComment = Comment.createComment()
                    .member(member)
                    .post(post)
                    .content("댓글"+i)
                    .parent(null)
                    .build();
            commentRepository.save(parentComment);

            if (i%3==0) {
                for (int j=0; j<10; j++) {
                    Comment childComment = Comment.createComment()
                            .member(member)
                            .post(post)
                            .content("대댓글"+j)
                            .parent(parentComment)
                            .build();
                    commentRepository.save(childComment);
                    if (j%3==0) {
                        LikeComment likeComment2 = new LikeComment(childComment, member, true);
                        likeCommentRepository.save(likeComment2);
                    }
                }
                parentId = parentComment.getId();
            }
        }

        em.flush();
        em.clear();
        // when
        Page<CommentWithIsLikeDto> results = commentRepository.findChildCommentWithIsLikeDto(member.getId(), post.getId(), parentId, PageRequest.of(1, 5));

        for (CommentWithIsLikeDto c: results) {
            log.info("c={}", c);
        }

    }


//    @BeforeTestExecution
//    void getMember() {
//        Member member = Member.createMember()
//            .name("solmin")
//            .email("solmin3665@gmail.com")
//            .role(Role.ROLE_USER)
//            .profileImgUrl("awefawefa")
//            .build();
//        memberRepository.save(member);
//
//        for (int i = 0; i < 4; i++) {
//            postRepository.save(Post.createPost()
//                .title("게시글" + i)
//                .category(PostCategory.DFS_BFS)
//                .content("<p>bfs어려워요" + i + "</p")
//                .member(member)
//                .build()
//            );
//        }
//
//
//        em.flush();
//        em.clear();
//    }
}