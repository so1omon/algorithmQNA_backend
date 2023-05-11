package algorithm_QnA_community.algorithm_QnA_community.domain.response;

import lombok.*;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.response
 * fileNmae         : DefStatus
 * author           : janguni
 * date             : 2023-05-02
 * description      : response status 객체
 *
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/04/20       janguni         최초 생성
 */

@Data
@RequiredArgsConstructor
public class DefStatus {
    private int code;
    private String message;

    public DefStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
