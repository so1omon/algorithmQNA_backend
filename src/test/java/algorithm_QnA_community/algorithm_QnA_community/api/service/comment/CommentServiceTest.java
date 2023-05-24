package algorithm_QnA_community.algorithm_QnA_community.api.service.comment;


import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostCreateReq;
import algorithm_QnA_community.algorithm_QnA_community.api.service.post.PostService;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import algorithm_QnA_community.algorithm_QnA_community.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Optional;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.post
 * fileName       : PostServiceTest
 * author         : janguni
 * date           : 2023/05/18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/18        janguni           최초 생성
 */

@SpringBootTest
@Slf4j
@Transactional
class CommentServiceTest {
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
    private ReportCommentRepository reportCommentRepository;

    @Autowired
    EntityManager em;


    @Test
    @Transactional
    void  댓글_신고(){
        Member reportedMember = Member.createMember()
                .name("uni2")
                .email("uni1234567@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("profile")
                .build();
        memberRepository.save(reportedMember);

        Member reportingMember = Member.createMember()
                .name("uni2")
                .email("uni1234567@gmail.com")
                .role(Role.ROLE_USER)
                .profileImgUrl("profile")
                .build();
        memberRepository.save(reportingMember);

        Post post = Post.createPost()
                .member(reportedMember)
                .title("게시물")
                .content("게시물 내용")
                .category(PostCategory.DP)
                .type(PostType.QNA)
                .build();
        postRepository.save(post);

        Comment comment = Comment.createComment()
                .member(reportedMember)
                .post(post)
                .content("<p>댓글</p>")
                .parent(null)
                .build();
        commentRepository.save(comment);

        ReportReq commentReportReq = new ReportReq("ETC", null);
        commentService.reportComment(comment.getId(), commentReportReq, reportingMember);

        em.flush();
        em.clear();

        Optional<ReportComment> findReportComment = reportCommentRepository.findByCommentIdAndMemberId(comment.getId(), reportingMember.getId());
        Assertions.assertThat(findReportComment.get().getDetail()).isEqualTo("기타 사유 없음");

    }
}