package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.admin
 * fileName       : ReportedPostListRes
 * author         : solmin
 * date           : 2023/05/18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/18        solmin       최초 생성
 * 2023/05/23        solmin       totalPageSize 수정 및 필드명 변경
 */

@Data
@AllArgsConstructor
public class ReportedPostsRes {
    private List<ReportedPostDto> reportedPosts;
    private int page;
    private boolean next;
    private boolean prev;
    private int size;
    private int totalPageSize;

    @Builder
    public ReportedPostsRes(Page<Post> postPage){
        this.page = postPage.getPageable().getPageNumber();
        this.next = postPage.hasNext();
        this.prev = postPage.hasPrevious();
        this.reportedPosts = postPage.stream().map(ReportedPostDto::new).collect(Collectors.toList());
        this.size = reportedPosts.size();
        this.totalPageSize = postPage.getTotalPages();
    }
}
