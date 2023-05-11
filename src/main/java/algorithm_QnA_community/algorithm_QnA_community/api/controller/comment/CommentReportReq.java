package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportCategory;
import algorithm_QnA_community.algorithm_QnA_community.utils.annotation.EnumValidator;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.comment
 * fileName       : CommentReportReq
 * author         : solmin
 * date           : 2023/05/09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/09        solmin       최초 생성
 *                                Enum Value를 최초에 String으로 받고 이에 대한 검증 어노테이션 적용
 *                                이후 Service단에서 Enum.valueOf로 치환해서 엔티티 생성 또는 수정
 * 2023/05/11        solmin       detail 기본값 넣기, StringUtils로 빈 데이터 들어올 때 기본값으로 들어가기
 */
@Data
@RequiredArgsConstructor
public class CommentReportReq {

    @EnumValidator(target = ReportCategory.class, message = "올바른 카테고리를 입력하세요.")
    private String category;

    private String detail = "기타 사유 없음";

    public void setDetail(String detail) {
        if(!StringUtils.isBlank(detail)) this.detail = detail;
    }

}
