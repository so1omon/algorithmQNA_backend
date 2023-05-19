package algorithm_QnA_community.algorithm_QnA_community.api.controller;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller
 * fileName       : MemberDetail
 * author         : solmin
 * date           : 2023/05/19
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/19        solmin       최초 생성
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberBriefDto {
    private Long memberId;
    private String memberName;
    private Role memberRole;
    private int memberCommentBadge;
    private int memberPostBadge;
    private int memberLikeBadge;
    private String memberProfileUrl;


    public MemberBriefDto(Member member){
        this.memberId = member.getId();
        this.memberName = member.getName();
        this.memberRole = member.getRole();
        this.memberCommentBadge = member.getCommentBadgeCnt();
        this.memberPostBadge =member.getPostBadgeCnt();
        this.memberLikeBadge = member.getLikeBadgeCnt();
        this.memberProfileUrl = member.getProfileImgUrl();
    }
}
