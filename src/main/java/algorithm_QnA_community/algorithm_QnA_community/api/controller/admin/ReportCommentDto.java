package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.MemberBriefDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.admin
 * fileName       : ReportCommentDto
 * author         : solmin
 * date           : 2023/05/23
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/23        solmin       최초 생성
 */

@Data
@RequiredArgsConstructor
public class ReportCommentDto {
    private Long reportCommentId;
    private MemberBriefDto member;
    private ReportCategory category;
    private String detail;
    private LocalDateTime updatedAt;

    public ReportCommentDto(ReportComment reportComment){
        this.reportCommentId = reportComment.getId();
        this.member = new MemberBriefDto(reportComment.getMember());
        this.category = reportComment.getReportCategory();
        this.detail = reportComment.getDetail();
        this.updatedAt = reportComment.getLastModifiedDate();
    }

}
