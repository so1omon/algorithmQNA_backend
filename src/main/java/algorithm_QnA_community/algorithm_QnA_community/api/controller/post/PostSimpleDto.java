package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import com.querydsl.core.annotations.QueryProjection;
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
@NoArgsConstructor
public class PostSimpleDto {
    private Long postId;
    private String title;
    private Long memberId;
    private String memberName;
    private String memberProfileUrl;
    private LocalDateTime createdAt;
    private int viewCount;

    private int commentCount;

    @QueryProjection
    public PostSimpleDto(Long postId, String title, Long memberId, String memberName, String memberProfileUrl, LocalDateTime createdAt, int viewCount, int commentCount) {
        this.postId = postId;
        this.title = title;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberProfileUrl = memberProfileUrl;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }
}
