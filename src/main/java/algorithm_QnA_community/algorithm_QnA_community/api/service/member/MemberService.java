package algorithm_QnA_community.algorithm_QnA_community.api.service.member;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.PostPageRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.member.CommentPageRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.repository.CommentRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.member
 * fileName       : MemberService
 * author         : solmin
 * date           : 2023/05/29
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/29        solmin       최초 생성
 * 2023/06/01        solmin       내가 작성한 게시글, 댓글 조회 API 추가
 */
@Service
@RequiredArgsConstructor
public class MemberService {
    static final int MAX_POST_SIZE = 20;
    static final int MAX_COMMENT_SIZE = 20;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    @Transactional
    public void updateMemberName(Member loginMember, String memberName) {
        loginMember.updateName(memberName);
    }

    @Transactional(readOnly = true)
    public PostPageRes getPosts(int page, Member member) {

        return new PostPageRes(postRepository
            .findByMemberOrderByCreatedDateDesc(member, PageRequest.of(page, MAX_POST_SIZE)));
    }

    @Transactional(readOnly = true)
    public CommentPageRes getComments(int page, Member member) {
        return new CommentPageRes(commentRepository
            .findByMemberOrderByCreatedDateDesc(member, PageRequest.of(page, MAX_COMMENT_SIZE)));
    }
}
