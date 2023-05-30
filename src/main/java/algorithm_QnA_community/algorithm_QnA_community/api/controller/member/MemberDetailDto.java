package algorithm_QnA_community.algorithm_QnA_community.api.controller.member;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.member
 * fileName       : MemberDetailDto
 * author         : solmin
 * date           : 2023/05/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/26        solmin       최초 생성
 *                                MemberBriefDto + 날짜정보 및 이메일 정보
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailDto {
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role memberRole;
    private int memberCommentBadge;
    private int memberPostBadge;
    private int memberLikeBadge;
    private String memberProfileUrl;


    public MemberDetailDto(Member member){
        this.memberId = member.getId();
        this.memberName = member.getName();
        this.memberRole = member.getRole();
        this.memberCommentBadge = member.getCommentBadgeCnt();
        this.memberPostBadge =member.getPostBadgeCnt();
        this.memberLikeBadge = member.getLikeBadgeCnt();
        this.memberProfileUrl = member.getProfileImgUrl();
        this.memberEmail = member.getEmail();
        this.createdAt = member.getCreatedDate();
        this.updatedAt = member.getLastModifiedDate();
    }
}
