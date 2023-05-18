package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.comment
 * fileName       : CommentListRes
 * author         : solmin
 * date           : 2023/05/16
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/16        solmin       최초 생성 (DTO이름 추후 변경필요)
 * 2023/05/18        solmin       필드명 변경
 */
@Data
@AllArgsConstructor
public class CommentListRes {
    private Long postId;
    private List<TopCommentRes> comments;
    private int page;
    private boolean next;
    private boolean prev;
    private int size;

    @Builder
    public CommentListRes(Long postId, Page<Comment> commentPage, List<TopCommentRes> comments){
        this.postId = postId;
        this.page = commentPage.getPageable().getPageNumber();
        this.next = commentPage.hasNext();
        this.prev = commentPage.hasPrevious();
        this.size = comments.size();
        this.comments = comments;
    }
}
