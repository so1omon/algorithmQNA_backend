package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.comment
 * fileName       : TopCommentRes
 * author         : solmin
 * date           : 2023/05/16
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/16        solmin       최초 생성 (DTO이름 추후 변경필요)
 *                                depth=0인 댓글정보 보여주기 위한 Dto
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopCommentRes extends CommentRes{
    private List<CommentRes> childCommentList = new ArrayList<>();
    private int childSize;

    public void addChild(CommentRes childCommentRes){
        this.childCommentList.add(childCommentRes);
        childSize++;
        if(!isHasChild()){
            setHasChild(true);
        }
    }

    public TopCommentRes(Comment comment){
        super(comment);
    }

}
