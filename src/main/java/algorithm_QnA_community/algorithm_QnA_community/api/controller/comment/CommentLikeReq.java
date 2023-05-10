package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
@NoArgsConstructor
public class CommentLikeReq {
    @NotNull
    private Boolean isLike;

    @NotNull
    private Boolean cancel;
}
