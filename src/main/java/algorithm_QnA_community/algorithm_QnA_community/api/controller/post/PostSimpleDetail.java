package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostSimpleDetail
 * author         : janguni
 * date           : 2023/05/17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/17        janguni           최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSimpleDetail {
    private Long postId;
    private String title;
    private Long memberId;
    private String memberName;
    private String memberProfileUrl;
    private LocalDateTime createdAt;
    private int viewCount;
    private int commentCount;
}
