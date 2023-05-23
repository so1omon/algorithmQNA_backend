package algorithm_QnA_community.algorithm_QnA_community.api.service.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.ReportedPostsRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import algorithm_QnA_community.algorithm_QnA_community.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

//import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.admin
 * fileName       : AdminServiceTest
 * author         : solmin
 * date           : 2023/05/22
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/22        solmin       최초 생성
 */
@SpringBootTest
//@Rollback
@Slf4j
class AdminServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ReportPostRepository reportPostRepository;
    @Autowired
    AdminService adminService;

    @Autowired
    EntityManager em;

    void clear() {
        em.flush();
        em.clear();
    }

    @Test
    @Transactional
    public void 신고된_게시물_목록_조회_테스트() {
        // 1. 멤버 만들기

        Member member1 = memberRepository.save(Member.createMember()
            .name("solmin")
            .email("solmin3665@gmail.com")
            .role(Role.ROLE_ADMIN)
            .profileImgUrl("awefawefa")
            .build());
        Member member2 = memberRepository.save(Member.createMember()
            .name("yoonhee")
            .email("yooonhee@gmail.com")
            .role(Role.ROLE_USER)
            .build());

        // 2. 게시물 15개 만들기
        for(int i=0;i<15;i++){
            postRepository.save(Post.createPost()
                .member(member1)
                .content("weafaweef")
                .title("awefeawf")
                .category(PostCategory.DFS_BFS)
                .type(PostType.QNA)
                .build()
            );
        }
        List<Post> posts = postRepository.findAll();

        // 3. 멤버가 게시물 신고
        for(int i=0;i<15;i++) {
            reportPostRepository.save(ReportPost.createReportPost()
                .category(ReportCategory.AD)
                .detail("ewafewaw")
                .member(member2)
                .post(posts.get(i))
                .build());
        }
        clear();
        // 4. adminService 메소드 사용
        ReportedPostsRes reportedPosts = adminService.getReportedPosts(0);

        assertThat(reportedPosts.getReportedPosts().size()).isEqualTo(10);
//        log.info("==============================: {}",reportedPosts.toString());
    }


}