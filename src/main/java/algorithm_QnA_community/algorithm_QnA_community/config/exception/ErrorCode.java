package algorithm_QnA_community.algorithm_QnA_community.config.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.config.exception
 * fileName       : ErrorCode
 * author         : solmin
 * date           : 2023/05/09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/09        solmin       UNAUTHORIZED (권한 없음), DUPLICATED_TASK(중복 채택 등),
 *                                REPORT_MY_RESOURCE(내 댓글 또는 글 신고) 코드 추가
 *
 */
@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  // COMMON
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, 404),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401),
  DUPLICATED_TASK(HttpStatus.BAD_REQUEST, 400),
  REPORT_MY_RESOURCE(HttpStatus.BAD_REQUEST, 400);


  private HttpStatus status;
  private int code;

  ErrorCode(HttpStatus status, int code) {
    this.status = status;
    this.code = code;
  }


  public int getValue() {
    return this.code;
  }
}