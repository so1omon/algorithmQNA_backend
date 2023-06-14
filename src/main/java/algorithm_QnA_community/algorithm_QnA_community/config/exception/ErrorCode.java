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
 * 2023/06/14        janguni      WRONG_POST_ID(댓글 하이라이팅 시 게시물 번호와 댓글의 번호 불일치) 추가
 */
@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
  DUPLICATED_TASK(HttpStatus.BAD_REQUEST),
  REPORT_MY_RESOURCE(HttpStatus.BAD_REQUEST),
  EMPTY_DETAIL_IN_ETC_REPORT(HttpStatus.BAD_REQUEST),
  INCOMPATIBLE_PARAMETER(HttpStatus.BAD_REQUEST),
  S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_EXTENSION(HttpStatus.BAD_REQUEST),
  DELAYED_UPLOAD(HttpStatus.BAD_REQUEST),

  WRONG_POST_ID(HttpStatus.BAD_REQUEST);

  private HttpStatus status;
  private int code;

  ErrorCode(HttpStatus status) {
    this.status = status;
    this.code = status.value();
  }


  public int getValue() {
    return this.code;
  }
}