package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.MemberBriefDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.admin
 * fileName       : ReportPostInfoRes
 * author         : solmin
 * date           : 2023/05/22
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/22        solmin       최초 생성
 */
@Data
@AllArgsConstructor
public class ReportedPostDetailRes {
    private Long postId;
    private MemberBriefDto member;
    private List<ReportPostDto> PostReports;
    private int page;
    private boolean next;
    private boolean prev;
    private int size;
    private int totalPageSize;
    private int totalReportedCnt;
    @Builder
    public ReportedPostDetailRes(Page<ReportPost> reportPostPage, Post post){
        this.postId = post.getId();
        this.member = new MemberBriefDto(post.getMember());
        this.page = reportPostPage.getPageable().getPageNumber();
        this.next = reportPostPage.hasNext();
        this.prev = reportPostPage.hasPrevious();
        this.PostReports = reportPostPage.stream().map(ReportPostDto::new).collect(Collectors.toList());
        this.size = reportPostPage.getSize();
        this.totalPageSize = reportPostPage.getTotalPages();
        this.totalReportedCnt = (int)reportPostPage.getTotalElements();
    }
}
