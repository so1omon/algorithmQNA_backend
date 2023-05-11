package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.comment
 * fileName       : CommentResultRes
 * author         : solmin
 * date           : 2023/05/09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/09        solmin       최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResultRes {
    private Long commentId;
    private Long commentMemberId;
    private String commentMemberName;
    private LocalDateTime updatedAt;
}
