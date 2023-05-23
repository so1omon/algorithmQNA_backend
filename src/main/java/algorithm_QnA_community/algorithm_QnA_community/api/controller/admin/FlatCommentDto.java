package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.MemberBriefDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.admin
 * fileName       : FlatCommentDto
 * author         : solmin
 * date           : 2023/05/22
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/22        solmin       최초 생성, 하위 댓글정보를 배제한 단순 Comment Dto
 */
@Data
@AllArgsConstructor
public class FlatCommentDto {
    private Long commentId;
    private MemberBriefDto member;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCnt;
    private int dislikeCnt;

    public FlatCommentDto(Comment comment){
        this.commentId = comment.getId();
        this.member = new MemberBriefDto(comment.getMember());
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedDate();
        this.updatedAt = comment.getLastModifiedDate();
        this.likeCnt=comment.getLikeCnt();
        this.dislikeCnt=comment.getDislikeCnt();
    }
}
