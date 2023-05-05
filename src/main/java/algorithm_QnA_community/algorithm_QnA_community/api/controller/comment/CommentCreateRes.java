package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.comment
 * fileName       : CommentCreateRes
 * author         : solmin
 * date           : 2023/05/04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/04        solmin       최초 생성
 */
@Data
public class CommentCreateRes {
    private Long commentId;
    private LocalDateTime createdAt;
    private int depth;

    public CommentCreateRes(Comment comment) {
        this.commentId = comment.getId();
        this.createdAt = comment.getCreatedDate();
        this.depth = comment.getDepth();
    }
//    public CommentCreateRes(Long commentId, LocalDateTime createdAt, int depth) {
//        this.commentId = commentId;
//        this.createdAt = createdAt;
//        this.depth = depth;
//    }
}
