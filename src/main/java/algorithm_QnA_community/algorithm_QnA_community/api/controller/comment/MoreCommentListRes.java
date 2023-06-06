package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.comment
 * fileName       : MoreCommentListRes
 * author         : solmin
 * date           : 2023/05/16
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/16        solmin       최초 생성 (DTO이름 추후 변경필요)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoreCommentListRes {
    private Long parentId;
    private List<CommentRes> childCommentList = new ArrayList<>();
    private int page;
    private boolean next;
    private boolean prev;
    private int size;
    public MoreCommentListRes(Long parentId,List<CommentRes> childCommentList, Page<Comment> commentPage){
        this.parentId = parentId;
        this.page = commentPage.getPageable().getPageNumber();
        this.next = commentPage.hasNext();
        this.prev = commentPage.hasPrevious();
        this.size = childCommentList.size();
        this.childCommentList = childCommentList;
    }

}
