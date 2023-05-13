package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostLikeReq
 * author         : janguni
 * date           : 2023/05/13
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/13        janguni           최초 생성
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeReq {

    @NotNull(message = "추천/비추천 정보가 누락되었습니다.")
    private Boolean isLike;

    @NotNull(message = "나의 반응삭제 정보가 누락되었습니다.")
    private Boolean cancel;
}
