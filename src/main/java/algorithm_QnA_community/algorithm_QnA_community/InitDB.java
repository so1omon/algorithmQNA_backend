package algorithm_QnA_community.algorithm_QnA_community;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.repository.CommentRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.LikeCommentRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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
 * 2023/05/05        solmin       [공지] 애플리케이션 로드 시에 DB초기값 삽입하는 코드입니다
 *                                초기값 삽입 원하지 않으시면 비활성화시켜주세요~~
 * 2023/05/10        solmin       댓글 삽입 init method 추가
 * 2023/05/16        solmin       데이터 계층적으로 10*10*10개 정도 생성
 */

@Component
@RequiredArgsConstructor
public class InitDB {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInitWithMemberAndPost();
        initService.dbInitWithComment();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final MemberRepository memberRepository;
        private final PostRepository postRepository;
        private final CommentRepository commentRepository;

        public void dbInitWithMemberAndPost() {
            Member member1 = Member.createMember()
                .name("solmin")
                .email("solmin3665@gmail.com")
                .role(Role.ROLE_ADMIN)
                .build();

            Member member2 = Member.createMember()
                .name("yoonhee")
                .email("yooonhee@gmail.com")
                .role(Role.ROLE_USER)
                .build();
            memberRepository.save(member1);
            memberRepository.save(member2);

            for (int i = 0; i < 4; i++) {
                postRepository.save(Post.createPost()
                    .title("게시글" + i)
                    .category(PostCategory.DFS_BFS)
                    .content("<p>bfs어려워요" + i + "</p")
                    .member(member1)
                    .build()
                );
            }


        } // 멤버, 게시글

        public void dbInitWithComment() {
            Member member1 = memberRepository.findById(1L).get();
            Member member2 = memberRepository.findById(2L).get();

            List<Member> members = new ArrayList<>();
            for(int i=2;i<22;i++){
                Member build = Member.createMember()
                    .name("testMember" + i)
                    .email("testMember" + i + "@gmail.com")
                    .role(Role.ROLE_USER)
                    .build();

                memberRepository.save(build);
                members.add(build);
            }
            Post post = member1.getPosts().get(0);

            for (int i = 0; i < 2; i++) {
                commentRepository.save(Comment.createComment()
                        .member(member1)
                        .content("weanfjakwlenefa")
                        .post(post)
                        .build()
                );
            }
            for (int i = 0; i < 2; i++) {
                commentRepository.save(Comment.createComment()
                    .member(member2)
                    .content("weanfjakwlenefa")
                    .post(post)
                    .build()
                );
            }

            Post tempPost = member1.getPosts().get(1);
            for(int i=0;i<10;i++){

                Comment save = commentRepository.save(Comment.createComment()
                    .member(member1)
                    .content("댓글 " + (i + 1))
                    .post(tempPost)
                    .build()
                );

                for(int j=0;j<20;j++) {
                    Comment save1 = commentRepository.save(Comment.createComment()
                        .member(members.get(j))
                        .content("대댓글 " + (j + 1))
                        .post(tempPost)
                        .parent(save)
                        .build()
                    );

                    for(int k=0;k<3;k++) {
                        commentRepository.save(Comment.createComment()
                            .member(member1)
                            .content("대대댓글 " + (k + 1))
                            .post(tempPost)
                            .parent(save1)
                            .build()
                        );
                    }

                }


            }
            for(int i=11;i<14;i++) {

                Comment save = commentRepository.save(Comment.createComment()
                    .member(member1)
                    .parent(commentRepository.findById(33L).get())
                    .content("대대대댓글 " + (i + 1))
                    .post(tempPost)
                    .build()
                );
            }


        } // 댓글
    }
}
