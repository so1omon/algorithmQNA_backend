package algorithm_QnA_community.algorithm_QnA_community.api.controller.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.service.admin.AdminService;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.admin
 * fileName       : AdminApiController
 * author         : solmin
 * date           : 2023/05/11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/11        solmin       최초 생성
 * 2023/05/23        solmin       관리자 API 구현 완료
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
@Validated
public class AdminApiController {

    private final AdminService adminService;
    @GetMapping("/post")
    public Res<PostPageRes> getReportedPosts(@RequestParam(required = false, name = "page", defaultValue = "0")
                                                      @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page){
        PostPageRes result = adminService.getReportedPosts(page);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 신고된 게시글을 조회했습니다."), result);
    }

    @GetMapping("/comment")
    public Res<ReportedCommentsRes> getReportedComments(@RequestParam(required = false, name = "page", defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page){
        ReportedCommentsRes result = adminService.getReportedComments(page);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 신고된 댓글을 조회했습니다."), result);
    }

    @GetMapping("/post/{post_id}")
    public Res<ReportedPostDetailRes> getReportPostInfo(@PathVariable("post_id") Long postId,
                                                        @RequestParam(required = false, name = "page", defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page){
        ReportedPostDetailRes result = adminService.getReportPostInfo(postId, page);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 신고된 게시글 상세내용을 조회했습니다."), result);
    }

    @GetMapping("/comment/{comment_id}")
    public Res<ReportedCommentDetailRes> getReportCommentInfo(@PathVariable("comment_id") Long commentId,
                                                        @RequestParam(required = false, name = "page", defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page){
        ReportedCommentDetailRes result = adminService.getReportCommentInfo(commentId, page);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 신고된 게시글 상세내용을 조회했습니다."), result);
    }

    @GetMapping("/admin/notice")
    public Res<PostPageRes> getNotices(@RequestParam(required = false, name = "page", defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
                                       @RequestParam(required = false, name = "postCategory") String postCategory){

        PostPageRes result;
        if(postCategory==null){
            result = adminService.getNotices(page);
        }else{
            result = adminService.getNotices(page, postCategory);
        }

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 공지사항을 조회했습니다."), result);
    }


    @DeleteMapping("/report/post/{report_post_id}")
    public Res deleteReportPost(@PathVariable("report_post_id") Long reportPostId){
        adminService.deleteReportPost(reportPostId);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물 신고 정보를 삭제했습니다."));
    }

    @DeleteMapping("/report/comment/{report_comment_id}")
    public Res deleteReportComment(@PathVariable("report_comment_id") Long reportCommentId){
        adminService.deleteReportComment(reportCommentId);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글 신고 정보를 삭제했습니다."));
    }

    @DeleteMapping("/post/{post_id}")
    public Res deleteReportedPost(@PathVariable("post_id") Long postId){
        adminService.deleteReportedPost(postId);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 신고된 게시물을 삭제했습니다."));
    }

    @DeleteMapping("/comment/{comment_id}")
    public Res deleteReportedComment(@PathVariable("comment_id") Long commentId){
        adminService.deleteReportedComment(commentId);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 신고된 댓글을 삭제했습니다."));
    }

}
