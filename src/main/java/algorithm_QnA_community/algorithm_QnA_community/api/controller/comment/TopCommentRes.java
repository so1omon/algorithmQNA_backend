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
 * 2023/05/19        solmin       생성자 변경
 * 2023/06/02        solmin       댓글, 게시글 조회 페이징 형태로 변경
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopCommentRes extends CommentRes{
    private List<CommentRes> childCommentList = new ArrayList<>();
    private int childSize;
    private int page = 0;
    private boolean next;
    private boolean prev = false;
    private int totalPageSize;

    public void addChild(CommentRes childCommentRes){
        this.childCommentList.add(childCommentRes);
        childSize++;
        if(!isHasChild()){
            setHasChild(true);
        }
    }

    public TopCommentRes updatePageInfo(int totalCnt){
        this.totalPageSize = totalCnt==0?0:(totalCnt-1)/10;
        this.next = totalPageSize>0?true:false;
        return this;
    }

    public TopCommentRes(Comment comment, Boolean isLiked){
        super(comment, isLiked);
    }

}
