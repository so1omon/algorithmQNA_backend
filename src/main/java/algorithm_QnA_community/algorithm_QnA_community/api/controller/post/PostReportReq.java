package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportCategory;
import algorithm_QnA_community.algorithm_QnA_community.utils.annotation.EnumValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringUtils;
/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostReportReq
 * author         : janguni
 * date           : 2023/05/13
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/13        janguni           최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostReportReq {

    @EnumValidator(target = ReportCategory.class, message = "올바른 카테고리를 입력하세요.")
    private String category;

    private String detail = "기타 사유 없음";



    public void setDetail(String detail) {
        if(!StringUtils.isBlank(detail)) this.detail = detail;
    }

}