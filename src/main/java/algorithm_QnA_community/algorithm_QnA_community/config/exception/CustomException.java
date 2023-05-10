package algorithm_QnA_community.algorithm_QnA_community.config.exception;


import lombok.Getter;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.config.Exception
 * fileName       : CustomException
 * author         : solmin
 * date           : 2023/05/05
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/05        solmin       최초 생성 - JAVA & Spring 내부 Exception으로 처리 불가능할 때
 *                                CustomException + ErrorCode + ExceptionHandlerAdvice 이용해서 예외처리
 */


@Getter
public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
    public CustomException(ErrorCode errorCode, String message){
        super(message);

        this.errorCode = errorCode;
    }

}
