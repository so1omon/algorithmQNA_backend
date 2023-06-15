package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentsRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailWithHighlightCommentRes extends PostDetailRes{
    private Long highlightCommentId;

    public PostDetailWithHighlightCommentRes(Post post, Member member, Boolean isLiked, CommentRes pinnedCommentRes, CommentsRes commentsRes, int totalCommentCnt, Long highlightCommentId) {
        super(post, member, isLiked, commentsRes, totalCommentCnt, pinnedCommentRes);
        this.highlightCommentId = highlightCommentId;
    }
}
