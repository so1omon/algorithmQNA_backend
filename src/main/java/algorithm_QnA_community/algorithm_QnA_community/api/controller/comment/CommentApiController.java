package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.service.comment.CommentService;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.PrincipalDetails;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
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
import javax.validation.constraints.Min;

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
 * 2023/05/19        solmin       공통 DTO 참조 변경
 * 2023/05/19        solmin       조회 api 사용 시 멤버 ID 넘겨주는 것으로 변경
 * 2023/05/23        solmin       page query parameter validation 추가
 * 2023/05/23        solmin       기타 사유 + 빈 신고사유 보내면 오류 처리하도록 변경
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentApiController {
    private final CommentService commentService;

    @GetMapping("/{post_id}")
    public Res<CommentsRes> getComments(@PathVariable("post_id") Long postId,
                                        @RequestParam(required = false, name = "page", defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
                                        Authentication authentication){


        Long memberId = getLoginMember(authentication).getId();
        CommentsRes result = commentService.getComments(postId, page, memberId);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글 내용을 조회했습니다."), result);
    }

    @GetMapping("/{comment_id}/spread")
    public Res<MoreCommentListRes> getMoreCommentsByParent(@PathVariable("comment_id") Long commentId,
                                                           @RequestParam(required = false, name = "page", defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
                                                           Authentication authentication) {

        Long memberId = getLoginMember(authentication).getId();

        MoreCommentListRes result = commentService.getMoreCommentsByParent(commentId, page, memberId);
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

                           @RequestBody @Valid LikeReq commentLikeReq, Authentication authentication){
        Res result = commentService.updateLikeInfo(commentId, commentLikeReq, getLoginMember(authentication));

        return result;
    }

    @PostMapping("/{comment_id}/report")
    public Res reportComment(@PathVariable("comment_id") Long commentId,

                           @RequestBody @Valid ReportReq commentReportReq, Authentication authentication){

        if(!commentReportReq.isValid) throw new CustomException(ErrorCode.EMPTY_DETAIL_IN_ETC_REPORT,
            "기타 카테고리 선택 시 상세 신고사유를 작성해야 합니다.");
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
