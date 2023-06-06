package algorithm_QnA_community.algorithm_QnA_community.api.controller.member;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.FlatCommentDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.PostWithContentDto;
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
 * fileName       : CommentPageRes
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
public class CommentPageRes {
    private List<FlatCommentDto> comments;
    private int page;
    private boolean next;
    private boolean prev;
    private int size;
    private int totalPageSize;

    public CommentPageRes(Page<Comment> commentPage){
        this.page = commentPage.getPageable().getPageNumber();
        this.next = commentPage.hasNext();
        this.prev = commentPage.hasPrevious();
        this.comments = commentPage.stream().map(FlatCommentDto::new).collect(Collectors.toList());
        this.size = comments.size();
        this.totalPageSize = commentPage.getTotalPages();
    }
}
