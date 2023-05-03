package algorithm_QnA_community.algorithm_QnA_community.domain.response;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config
 * fileNmae         : ResponseMessage
 * author           : janguni
 * date             : 2023-05-02
 * description      : ResponseMessage 모음
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/05/02       janguni         최초 생성
 */

public class ResponseMessage {
    public static final String SUCCESS_AUTHORIZE = "성공적으로 사용자 인증을 하였습니다.";

    public static final String FAIL_AUTHORIZE_CODE = "잘못된 인증코드 값 입니다.";

    public static final String EXPIRATION_TOKENS= "사용자 인증에 실패했습니다. 로그인 후 다시 시도해주세요.";

}
