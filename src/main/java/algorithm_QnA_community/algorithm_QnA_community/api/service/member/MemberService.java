package algorithm_QnA_community.algorithm_QnA_community.api.service.member;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
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
 */
@Service
public class MemberService {
    @Transactional
    public void updateMemberName(Member loginMember, String memberName) {
        loginMember.updateName(memberName);
    }
}
