package algorithm_QnA_community.algorithm_QnA_community;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community
 * fileName       : InitDB(초기 데이터 생성 용도)
 * author         : solmin
 * date           : 2023/05/04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/04        solmin       최초 생성
 */

@Component
@RequiredArgsConstructor
public class InitDB {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final MemberRepository memberRepository;
        private final PostRepository postRepository;

        public void dbInit() {
            Member member = Member.createMember()
                .name("solmin")
                .email("solmin3665@gmail.com")
                .role(Role.ROLE_USER)
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
        }
    }
}
