package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostSimpleRes
 * author         : janguni
 * date           : 2023/06/16
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/06/16        janguni           최초 생성
 * 2023/06/17        janguni           likeCnt, dislikeCnt 추가
 */
@Data
@AllArgsConstructor
public class PostSimpleRes {
    private Long postId;
    private String title;
    private Long memberId;
    private String memberName;
    private String memberProfileUrl;
    private LocalDateTime createdAt;
    private int viewCount;
    private int commentCount;

    private int likeCnt;
    private int dislikeCnt;

    public PostSimpleRes(PostSimpleDto postSimpleDto) {
        this.postId = postSimpleDto.getPostId();
        this.title = postSimpleDto.getTitle();
        this.memberId = postSimpleDto.getMemberId();
        this.memberName = postSimpleDto.getMemberName();
        this.memberProfileUrl = postSimpleDto.getMemberProfileUrl();
        this.createdAt = postSimpleDto.getCreatedAt();
        this.viewCount = postSimpleDto.getViewCount();
        this.commentCount = postSimpleDto.getCommentCount();
        this.likeCnt = postSimpleDto.getLikeCnt();
        this.dislikeCnt = postSimpleDto.getDislikeCnt();
    }
}
