package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.InitDB;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


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
 */

@SpringBootTest
@Transactional
@Rollback
class CommentRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

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