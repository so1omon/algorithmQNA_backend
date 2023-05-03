package algorithm_QnA_community.algorithm_QnA_community.domain.response;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.domain.response
 * fileNmae         : StatusCode
 * author           : janguni
 * date             : 2023-05-02
 * description      : StatusCode 모음
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/05/02       janguni         최초 생성
 */

public abstract class StatusCode {
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;

    public static final int BAD_REQUEST =  400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;

    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int DB_ERROR = 600;
}