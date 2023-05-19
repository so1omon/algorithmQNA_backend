package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentDetailRes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostDetailRes
 * author         : janguni
 * date           : 2023/05/14
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/14        janguni       최초 생성
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailRes {
    private Long postId;
    private Long memberId;
    private String memberName;
    private int memberCommentBadge;
    private int memberPostBadge;
    private int memberLikeBadge;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;
    private int disLikeCount;

    private int commentTotalCount;
    private int commentCurrentPage;
    private boolean commentNext;
    private boolean commentPrev;
    private int commentSize;
    private List<CommentDetailRes> comments;
}
