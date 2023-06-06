package algorithm_QnA_community.algorithm_QnA_community.domain.response;

import lombok.Data;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.response
 * fileNmae         : CodeAndState
 * author           : janguni
 * date             : 2023-05-02
 * description      : 개발 테스트 용  (삭제예정)
 *                      : /google/callback 에서 인증코드와 state값 body에 넣어줄 때 사용
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/04/20       janguni         최초 생성
 */

@Data
public class CodeAndState {
    private String code;
    private String state;

    public CodeAndState(String code, String state) {
        this.code = code;
        this.state = state;
    }
}
