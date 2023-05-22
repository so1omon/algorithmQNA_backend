package algorithm_QnA_community.algorithm_QnA_community.api.service.admin;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.repository.CommentRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

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
@Transactional
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
    AdminService adminService;

    @Autowired
    EntityManager em;

    void clear(){
        em.flush();
        em.clear();
    }

    @Test
    @Transactional
    public void 신고된_게시물_목록_조회_테스트() {
        // 1. 멤버 만들기

        memberRepository.save(Member.createMember()
            .name("solmin")
            .email("solmin3665@gmail.com")
            .role(Role.ROLE_ADMIN)
            .profileImgUrl("awefawefa")
            .build());
        memberRepository.save(Member.createMember()
            .name("yoonhee")
            .email("yooonhee@gmail.com")
            .role(Role.ROLE_USER)
            .build());

        clear();
        // 2. 게시물 하나 만들기
        postRepository.save(Post)

        // 3. 멤버가 게시물 신고
        // 4. adminService 메소드 사용





    }



}