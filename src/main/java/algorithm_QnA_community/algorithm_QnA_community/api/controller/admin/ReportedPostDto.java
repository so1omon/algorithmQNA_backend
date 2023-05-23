package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.MemberBriefDto;
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
 * 2023/05/23        solmin       API 스펙에 맞게 필드명 및 생성자 변경
 */
@Data
@AllArgsConstructor
public class ReportedPostDto {
    private Long postId;
    private String postTitle;
    private String postContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int postLikeCnt;
    private int postDislikeCnt;
    private int commentCnt;
    private int views;
    private MemberBriefDto member;

    public ReportedPostDto(Post post){
        this.postId = post.getId();
        this.member = new MemberBriefDto(post.getMember());
        this.postTitle = post.getTitle();
        this.postContent = post.getContent();
        this.createdAt = post.getCreatedDate();
        this.updatedAt = post.getLastModifiedDate();
        this.postLikeCnt = post.getLikeCnt();
        this.postDislikeCnt = post.getDislikeCnt();
        // 1페이지당 최대 10번만 countQuery 동작
        this.commentCnt = post.getComments().size();
        this.views = post.getViews();
    }
}
