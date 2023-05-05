package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
 */
@SpringBootTest
@Transactional
class PostRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

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
}