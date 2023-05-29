package algorithm_QnA_community.algorithm_QnA_community.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain.member
 * fileName       : Badge
 * author         : solmin
 * date           : 2023/05/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/25        solmin       최초 생성
 */

@AllArgsConstructor
@Getter
public enum Badge {
    POST(0),
    COMMENT(1),
    Like(2);

    int value;


}
