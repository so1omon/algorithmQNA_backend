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
 * 2023/05/01        solmin       노션 명세대로 변경, value()-> getValue()
 */
@AllArgsConstructor
public enum ReportCategory {
    SLANG("비속어"),
    POLITICAL("정치적 발언"),
    AD("광고 등 상업적인 목적"),
    INSULT("모욕"),
    LUSTFUL("음란성 게시물/댓글"),
    OUT_OF_TOPIC("주제와 맞지 않는 글/댓글"),
    OUT_OF_FORMAT("게시판 형식에 맞지 않음"),
    ETC("기타 사유");

    String value;

    public String getValue(){
        return this.value;
    }
}
