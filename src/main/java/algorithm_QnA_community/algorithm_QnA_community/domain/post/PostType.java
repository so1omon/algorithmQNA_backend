package algorithm_QnA_community.algorithm_QnA_community.domain.post;

import lombok.AllArgsConstructor;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain.post
 * fileName       : PostType
 * author         : janguni
 * date           : 2023/05/11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        janguni            최초 생성
 *
 */
@AllArgsConstructor
public enum PostType {

    QNA("질의응답"),
    TIP("꿀팁"),
    NOTICE("공지사항");

    String value;

    public String getValue(){
        return this.value;
    }
}
