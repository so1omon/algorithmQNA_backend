package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.MemberBriefDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.admin
 * fileName       : ReportPostDto
 * author         : solmin
 * date           : 2023/05/22
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/22        solmin       최초 생성
 */
@Data
@RequiredArgsConstructor
public class ReportPostDto {
    private Long reportPostId;
    private MemberBriefDto member;
    private ReportCategory category;
    private String detail;
    private LocalDateTime updatedAt;

    public ReportPostDto(ReportPost reportPost){
        this.reportPostId = reportPost.getId();
        this.member = new MemberBriefDto(reportPost.getMember());
        this.category = reportPost.getReportCategory();
        this.detail = reportPost.getDetail();
        this.updatedAt = reportPost.getLastModifiedDate();
    }

}
