package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;

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
 * 2023/05/10        solmin       DTO Validation 추가
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateReq {
    @NotBlank(message = "게시글 내용을 1글자 이상 작성해야 합니다.")
    private String content;

    private Long parentCommentId;
}
