package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.admin
 * fileName       : ReportedCommentsRes
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
public class ReportedCommentsRes {
    private List<FlatCommentDto> reportedComments;
    private int page;
    private boolean next;
    private boolean prev;
    private int size;
    private int totalPageSize;

    @Builder
    public ReportedCommentsRes(Page<Comment> commentPage){
        this.page = commentPage.getPageable().getPageNumber();
        this.next = commentPage.hasNext();
        this.prev = commentPage.hasPrevious();
        this.reportedComments = commentPage.stream().map(FlatCommentDto::new).collect(Collectors.toList());
        this.size = reportedComments.size();
        this.totalPageSize = commentPage.getTotalPages();

    }
}
