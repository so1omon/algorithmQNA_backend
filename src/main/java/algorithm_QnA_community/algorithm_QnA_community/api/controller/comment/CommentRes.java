package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.comment
 * fileName       : CommentRes
 * author         : solmin
 * date           : 2023/05/16
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/16        solmin       최초 생성 (DTO이름 추후 변경필요)
 *                                depth>=1인 댓글정보 보여주기 위한 Dto
 * 2023/05/18        solmin       dto에 언급자 정보 추가
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRes {
    private Long commentId;
    private Long memberId;
    private String memberName;
    private Long parentId;
    private Long mentionerId;
    private String mentionerName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCnt;
    private int dislikeCnt;
    private boolean hasChild = false;
    private boolean isPinned = false;
    private int depth;

    public CommentRes (Comment comment){
        this.commentId=comment.getId();
        this.memberId=comment.getMember().getId();
        this.memberName=comment.getMember().getName();
        if(comment.getParent()!=null) this.parentId = comment.getParent().getId();
        this.content=comment.getContent();
        this.createdAt=comment.getCreatedDate();
        this.updatedAt=comment.getLastModifiedDate();
        this.likeCnt=comment.getLikeCnt();
        this.dislikeCnt=comment.getDislikeCnt();
        this.depth=comment.getDepth();
        this.isPinned = comment.isPinned();
        if(comment.getMentioner()!=null){
            this.mentionerId = comment.getMentioner().getId();
            this.mentionerName = comment.getMentioner().getName();
        }
    }

}
