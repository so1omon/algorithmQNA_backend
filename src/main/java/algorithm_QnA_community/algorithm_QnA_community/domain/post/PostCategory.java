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
 * 2023/05/01        solmin       노션 명세대로 변경, value()-> getValue()
 *
 */
@AllArgsConstructor
public enum PostCategory {
    BRUTE_FORCE("완전탐색"),
    TWO_POINTER("투 포인터"),
    DP("다이나믹 프로그래밍"),
    QUEUE_STACK_HASH("큐/스택/해쉬"),
    GRAPH("그래프"),
    GREEDY("그리디 알고리즘"),
    BINARY_SEARCH("바이너리 서치"),
    SORT("정렬"),
    DFS_BFS("BFS/DFS");

    String value;

    public String getValue(){
        return this.value;
    }
}
