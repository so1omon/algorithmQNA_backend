package algorithm_QnA_community.algorithm_QnA_community.api.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller
 * fileName       : LikeReq
 * author         : janguni
 * date           : 2023/05/18
 * description    :         추천/비추천 정보 request
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/18        janguni           최초 생성
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeReq {

    @NotNull
    private Boolean isLike;

    @NotNull
    private Boolean cancel;

}
