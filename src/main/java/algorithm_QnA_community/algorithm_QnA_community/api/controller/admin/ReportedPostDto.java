package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.admin
 * fileName       : PostRes
 * author         : solmin
 * date           : 2023/05/18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/18        solmin       최초 생성
 */
@Data
@AllArgsConstructor
public class ReportedPostDto {
    private Long postId;
    private Long memberId;
    private String memberName;
    private Role memberRole;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCnt;
    private int dislikeCnt;
    private int commentCnt;
    private int views;

    public ReportedPostDto(Post post){
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.memberName = post.getMember().getName();
        this.memberRole = post.getMember().getRole();
        this.title = post.getTitle();
        this.createdAt = post.createdDate;
        this.updatedAt = post.lastModifiedDate;
        this.likeCnt = post.getLikeCnt();
        this.dislikeCnt = post.getDislikeCnt();
        // 1페이지당 최대 10번만 countQuery 동작
        this.commentCnt = post.getComments().size();
        this.views = post.getViews();
    }
}
