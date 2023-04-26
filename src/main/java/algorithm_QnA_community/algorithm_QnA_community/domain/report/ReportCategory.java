package algorithm_QnA_community.algorithm_QnA_community.domain.report;

import lombok.AllArgsConstructor;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain.category
 * fileName       : ReportCategory
 * author         : solmin
 * date           : 2023/04/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        solmin       최초 생성
 */
@AllArgsConstructor
public enum ReportCategory {
    ABUSAL("욕설"),
    POLITICAL("정치적 발언"),
    CALUMNY("비방"),
    AD("광고");

    String reason;

    public String value(){
        return this.reason;
    }
}
