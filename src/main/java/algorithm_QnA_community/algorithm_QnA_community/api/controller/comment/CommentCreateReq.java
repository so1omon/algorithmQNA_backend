package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.comment
 * fileName       : CommentCreateReq
 * author         : solmin
 * date           : 2023/05/04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/04        solmin       최초 생성
 */
@Data
@AllArgsConstructor
public class CommentCreateReq {
    private String content;
    private Long parentCommentId;

}
