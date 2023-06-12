package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;


import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentWithIsLikeDto {
    private Comment comment;
    private Boolean isLiked;

    public CommentWithIsLikeDto(Comment comment, Boolean isLike) {
        this.comment = comment;
        this.isLiked = isLike;
    }
}
