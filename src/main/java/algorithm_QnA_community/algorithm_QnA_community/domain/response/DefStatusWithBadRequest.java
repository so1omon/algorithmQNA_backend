package algorithm_QnA_community.algorithm_QnA_community.domain.response;

import lombok.*;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain.response
 * fileName       : DefStatusWithBadRequest
 * author         : solmin
 * date           : 2023/05/09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/09        solmin       최초 생성
 *                                BadRequest 시 validateError 필드를 위한 상속 객체
 */
@Data
@ToString(callSuper=true, includeFieldNames=true)
@AllArgsConstructor
public class DefStatusWithBadRequest extends DefStatus{
    private boolean validateError;
    public DefStatusWithBadRequest(int code, String message, boolean isValidateError) {
        super(code, message);
        this.validateError = isValidateError;
    }
}
