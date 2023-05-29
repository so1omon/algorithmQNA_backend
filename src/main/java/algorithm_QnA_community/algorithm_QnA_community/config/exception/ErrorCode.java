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
 * 2023/05/23        solmin       EMPTY_DETAIL_IN_ETC_REPORT(ETC+빈 신고사유) 추가
 */
@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  // COMMON
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, 404),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401),
  DUPLICATED_TASK(HttpStatus.BAD_REQUEST, 400),
  REPORT_MY_RESOURCE(HttpStatus.BAD_REQUEST, 400),
  EMPTY_DETAIL_IN_ETC_REPORT(HttpStatus.BAD_REQUEST, 400),
  INCOMPATIBLE_PARAMETER(HttpStatus.BAD_REQUEST, 400);

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