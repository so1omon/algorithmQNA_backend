package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.MemberBriefDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.admin
 * fileName       : ReportedCommentDetailRes
 * author         : solmin
 * date           : 2023/05/23
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/23        solmin       최초 생성
 */

@Data
@AllArgsConstructor
public class ReportedCommentDetailRes {
    private Long postId;
    private Long commentId;
    private MemberBriefDto member;
    private List<ReportCommentDto> commentReports;
    private int page;
    private int totalPageSize;
    private boolean next;
    private boolean prev;
    private int size;
    private int totalReportedCnt;
    @Builder
    public ReportedCommentDetailRes(Page<ReportComment> reportCommentPage, Comment comment){
        this.postId = comment.getPost().getId();
        this.commentId = comment.getId();
        this.member = new MemberBriefDto(comment.getMember());
        this.page = reportCommentPage.getPageable().getPageNumber();
        this.next = reportCommentPage.hasNext();
        this.prev = reportCommentPage.hasPrevious();
        this.commentReports = reportCommentPage.stream().map(ReportCommentDto::new).collect(Collectors.toList());
        this.size = reportCommentPage.getSize();
        this.totalPageSize = reportCommentPage.getTotalPages();
        this.totalReportedCnt = (int)reportCommentPage.getTotalElements();
    }
}