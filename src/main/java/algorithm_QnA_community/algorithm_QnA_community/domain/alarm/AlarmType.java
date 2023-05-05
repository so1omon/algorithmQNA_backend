package algorithm_QnA_community.algorithm_QnA_community.domain.alarm;

import lombok.AllArgsConstructor;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain.alarm
 * fileName       : AlarmType
 * author         : solmin
 * date           : 2023/05/01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/01        solmin       최초 생성
 */
@AllArgsConstructor
public enum AlarmType {
    COMMENT_WRITE("내가 쓴 글에 다른 사람이 댓글(depth 관계없이)을 작성하거나 내가 쓴 댓글에 다른 사람이 댓글(depth 1차이)을 작성할 때"),
    COMMENT_LIKE("내가 쓴 댓글에 다른 사람이 추천을 했을 때"),
    COMMENT_DISLIKE("내가 쓴 댓글에 다른 사람이 비추천 했을 때"),
    POST_LIKE("내가 쓴 글에 다른 사람이 추천을 했을 때"),
    POST_DISLIKE("내가 쓴 글에 다른 사람이 비추천을 했을 때"),
    PINNED("내가 쓴 댓글을 글 작성자가 채택했을 때"),
    DELETE_COMMENT("관리자에 의해서 내가 쓴 댓글이 삭제되었을 때"),
    DELETE_POST("관리자에 의해서 내가 쓴 글이 삭제되었을 때"),
    BADGE_UPGRADE("나의 뱃지 상태가 업그레이드됐을 때"),
    BADGE_DOWNGRADE("나의 뱃지 상태가 다운그레이드 됐을 때");

    String value;

    public String getValue(){
        return this.value;
    }
}
