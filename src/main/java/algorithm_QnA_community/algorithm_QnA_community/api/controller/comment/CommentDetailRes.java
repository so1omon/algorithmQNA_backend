package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.comment
 * fileName       : CommentDetailRes
 * author         : janguni
 * date           : 2023/05/15
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/15        janguni       최초 생성
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDetailRes {
    private Long commentId;
    private Long parentId;
    private Long memberId;
    private String memberName;
    private String memberProfile;
    private int memberCommentBadge;
    private int memberPostBadge;
    private int memberLikeBadge;

    private String content;
    private int likeCount;
    private int dislikeCount;
    private LocalDateTime createdAt;
    private int depth;
    private boolean isPinned;
    private boolean isLiked;
}
