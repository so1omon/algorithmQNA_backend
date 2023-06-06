package algorithm_QnA_community.algorithm_QnA_community.domain.post;

import lombok.AllArgsConstructor;


/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain.category
 * fileName       : PostSortType
 * author         : janguni
 * date           : 2023/05/16
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/16        janguni            최초 생성
 *
 */
@AllArgsConstructor
public enum PostSortType {
    LATESTASC("오래된 순"),
    LATESTDESC("최신순"),
    COMMENTCNTASC("댓글 적은 순"),
    COMMENTCNTDESC("댓글 많은 순"),
    LIKEASC("추천 낮은 순"),
    LIKEDESC("추천 높은 순"),
    VIEWCNTASC("조회수 낮은 순"),
    VIEWCNTDESC("조회수 높은 순"),
    POPULAR("인기순");

    String value;

    public String getValue(){
        return this.value;
    }
}
