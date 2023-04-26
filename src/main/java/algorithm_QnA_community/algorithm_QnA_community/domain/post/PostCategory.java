package algorithm_QnA_community.algorithm_QnA_community.domain.post;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain.category
 * fileName       : E
 * author         : solmin
 * date           : 2023/04/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        solmin       최초 생성
 */
@AllArgsConstructor
public enum PostCategory {
    SORT("정렬"),
    DFS("깊이 우선 탐색"),
    BFS("너비 우선 탐색"),
    BRUTE_FORCE("완전 탐색");

    String category;

    public String value(){
        return this.category;
    }
}
