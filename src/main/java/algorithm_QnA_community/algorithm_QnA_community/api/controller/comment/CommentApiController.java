package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.service.comment.CommentService;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.PrincipalDetails;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
 * 2023/05/11        solmin       response data 필요없는 부분들 전부 고침
 * 2023/05/15        solmin       controller 단에서 authentication 받아서 로그인한 유저 검증
 * 2023/05/16        solmin       댓글 조회, 댓글 펼쳐보기 API 구현 완료
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentApiController {
    private final CommentService commentService;

    @GetMapping("/{post_id}")
    public Res<CommentListRes> getComments(@PathVariable("post_id") Long postId,
                           @RequestParam(required = false, name = "page", defaultValue = "0") int page){


        CommentListRes result = commentService.getComments(postId, page);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글 내용을 조회했습니다."), result);
    }

    @GetMapping("/{comment_id}/spread")
    public Res<MoreCommentListRes> getMoreCommentsByParent(@PathVariable("comment_id") Long commentId,
                                           @RequestParam(required = false, name = "page", defaultValue = "0") int page){


        MoreCommentListRes result = commentService.getMoreCommentsByParent(commentId, page);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글 내용을 조회했습니다."), result);
    }

    @PostMapping("/{post_id}")
    public ResponseEntity<Res<CommentCreateRes>> writeComment(@PathVariable("post_id") Long postId,
                                                              @RequestBody @Valid CommentCreateReq commentCreateReq,
                                                              Authentication authentication){


        CommentCreateRes result= commentService.writeComment(postId, commentCreateReq, getLoginMember(authentication));

        return new ResponseEntity<>(Res.res(new DefStatus(HttpStatus.CREATED.value(), "성공적으로 댓글을 추가했습니다."),result),
            HttpStatus.CREATED);
    }

    @PostMapping("/{comment_id}/like")
    public Res likeComment(@PathVariable("comment_id") Long commentId,

                           @RequestBody @Valid CommentLikeReq commentLikeReq, Authentication authentication){
        Res result = commentService.updateLikeInfo(commentId, commentLikeReq, getLoginMember(authentication));

        return result;
    }

    @PostMapping("/{comment_id}/report")
    public Res reportComment(@PathVariable("comment_id") Long commentId,

                           @RequestBody @Valid CommentReportReq commentReportReq, Authentication authentication){

        commentService.reportComment(commentId, commentReportReq, getLoginMember(authentication));

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 신고했습니다."));
    }



    @PatchMapping("/{comment_id}")
    public Res updateComment(@PathVariable("comment_id") Long commentId,
                             @RequestBody @Valid CommentCreateReq commentCreateReq,
                             Authentication authentication){

        commentService.updateComment(commentId, commentCreateReq.getContent(), getLoginMember(authentication));

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 수정했습니다."));
    }

    @PatchMapping("/{comment_id}/pin")
    public Res pinComment(@PathVariable("comment_id") Long commentId, Authentication authentication){

        commentService.pinComment(commentId, getLoginMember(authentication));

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 채택했습니다."));
    }


    @DeleteMapping("/{comment_id}")
    public Res deleteComment(@PathVariable("comment_id") Long commentId, Authentication authentication){

        commentService.deleteComment(commentId, getLoginMember(authentication));

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 삭제했습니다."));
    }

    // authentication 객체를 넘겨주면 PrincipalDetails를 조회해서 Member정보를 넘겨줌
    private static Member getLoginMember(Authentication authentication) {
        Member loginMember = ((PrincipalDetails) authentication.getPrincipal()).getMember();
        return loginMember;
    }
}
