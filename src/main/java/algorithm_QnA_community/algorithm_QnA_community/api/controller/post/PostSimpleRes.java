package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
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
 * 2023/06/26        solmin            post 객체를 받는 Constructor 추가
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

    public PostSimpleRes(Post post) {
        Member member = post.getMember();
        this.postId = post.getId();
        this.title = post.getTitle();
        this.memberId = member.getId();
        this.memberName = member.getName();
        this.memberProfileUrl = member.getProfileImgUrl();
        this.createdAt = post.getCreatedDate();
        this.viewCount = post.getViews();
        this.commentCount = post.getComments().size();
        this.likeCnt = post.getLikeCnt();
        this.dislikeCnt = post.getDislikeCnt();
    }
}
