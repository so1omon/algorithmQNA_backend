package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;


import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentWithIsLikeDto {
    private Long commentId;
    private Long parentId;
    private String content;
    private Member member;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int likeCnt;
    private int dislikeCnt;

    private boolean hasChild;
    private int depth;
    private boolean isPinned;
    private Boolean isLiked;

    public CommentWithIsLikeDto(Comment comment, Boolean isLike) {
        commentId = comment.getId();
        if (comment.getDepth()!=0){
            parentId = comment.getParent().getId();
        } else{
            parentId = null;
        }
        content = comment.getContent();
        member = comment.getMember();
        createdAt = comment.getCreatedDate();
        updatedAt = comment.getLastModifiedDate();
        likeCnt = comment.getLikeCnt();
        dislikeCnt = comment.getDislikeCnt();
        hasChild = !comment.getChild().isEmpty();
        depth = comment.getDepth();
        isPinned = comment.isPinned();
        isLiked = isLike;
    }
}
