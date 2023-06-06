package algorithm_QnA_community.algorithm_QnA_community.config.exception;

import org.springframework.security.core.AuthenticationException;


/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config.Exception
 * fileNmae         : TokenAuthenticationException
 * author           : janguni
 * date             : 2023-05-02
 * description      : accessToken, refreshToken 둘 다 유효하지 않을 때
 *                    tokenAuthenticationFilter에서 발생하는 예외
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/05/02       janguni         최초 생성
 */
public class TokenAuthenticationException extends AuthenticationException {

    public TokenAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TokenAuthenticationException(String msg) {
        super(msg);
    }
}
