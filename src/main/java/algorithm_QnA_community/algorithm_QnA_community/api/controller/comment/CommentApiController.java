package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.api.service.comment.CommentService;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller
 * fileName       : PostApiController
 * author         : solmin
 * date           : 2023/05/04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/04        solmin       최초 생성
 * 2023/05/10        solmin       댓글 API 모두 구현 (유저인증만 남음)
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentApiController {
    private final CommentService commentService;

    @PostMapping("/{post_id}")
    public ResponseEntity<Res<CommentCreateRes>> writeComment(@PathVariable("post_id") Long postId,
                                                             @RequestBody @Valid CommentCreateReq commentCreateReq){

        CommentCreateRes result= commentService.writeComment(postId, commentCreateReq);

        return new ResponseEntity<>(Res.res(new DefStatus(HttpStatus.CREATED.value(), "성공적으로 댓글을 추가했습니다."),result),
            HttpStatus.CREATED);
    }

    @PostMapping("/{comment_id}/like")
    public Res likeComment(@PathVariable("comment_id") Long commentId,
                                                              @RequestBody @Valid CommentLikeReq commentLikeReq){
        Res result = commentService.updateLikeInfo(commentId, commentLikeReq);

        return result;
    }

    @PostMapping("/{comment_id}/report")
    public Res<CommentResultRes> reportComment(@PathVariable("comment_id") Long commentId,
                           @RequestBody @Valid CommentReportReq commentReportReq){
        CommentResultRes result = commentService.reportComment(commentId, commentReportReq);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 신고했습니다."),
            result);
    }

    @PatchMapping("/{comment_id}")
    public Res updateComment(@PathVariable("comment_id") Long commentId,
                                              @RequestBody @Valid CommentCreateReq commentCreateReq){

        commentService.updateComment(commentId, commentCreateReq.getContent());

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 수정했습니다."));
    }

    @PatchMapping("/{comment_id}/pin")
    public Res<CommentResultRes> pinComment(@PathVariable("comment_id") Long commentId){

        CommentResultRes result = commentService.pinComment(commentId);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 채택했습니다."), result);
    }


    @DeleteMapping("/{comment_id}")
    public Res deleteComment(@PathVariable("comment_id") Long commentId){

        commentService.deleteComment(commentId);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 삭제했습니다."),
            new Deleted(LocalDateTime.now()));
    }



    @Data
    static class Deleted{
        private LocalDateTime deletedAt;

        public Deleted(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
        }
    }
}
