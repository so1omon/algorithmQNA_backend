package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.service.post.PostService;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.PrincipalDetails;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.dto.UserDetailsImpl;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostSortType;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostApiController
 * author         : janguni
 * date           : 2023/05/11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/11        janguni            최초 생성
 * 2023/05/19        solmin             게시글 작성 시 postId, 작성일 정보 리턴
 * 2023/05/23        solmin             게시글 신고 시 validation 일부 추가
 * 2023/05/28        janguni            게시물 목록 조회 필터링 조건 추가
 * 2023/05/31        janguni            게시물 목록 조회 @RequestBody로 변경
 * 2023/06/01        janguni            게시물 목록 조회 @RequestParam으로 재변경
 * 2023/06/11        janguni            댓글 하이라이팅 api 추가
 * 2023/06/26        solmin             게시글 특성 구분없이 최근 10개의 목록 가져오는 API 추가
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@Slf4j
public class PostApiController {

    private final PostService postService;

    /**
     * 게시물 등록
     */

    @PostMapping
    public Res<PostWriteRes> writePost(@RequestBody @Valid PostCreateReq postCreateReq, Authentication authentication){
        Member findMember = getLoginMember(authentication);
        PostWriteRes result = postService.writePost(postCreateReq, findMember);
        return new Res(new DefStatus(StatusCode.CREATED, "성공적으로 게시물을 등록했습니다."),result);
    }


    /**
     * 게시물 수정
     */
    @PatchMapping("/{post_id}")
    public Res updatePost(@PathVariable("post_id") Long postId,
                           @RequestBody @Valid PostUpdateReq postUpdateReq, Authentication authentication){
        Member findMember = getLoginMember(authentication);
        postService.updatePost(postId,postUpdateReq, findMember);
        return new Res(new DefStatus(StatusCode.OK, "성공적으로 게시물을 수정했습니다."),null);
    }

    /**
     * 게시물 삭제
     */
    @DeleteMapping("/{post_id}")
    public Res deletePost(@PathVariable("post_id") Long postId, Authentication authentication){
        Member findMember = getLoginMember(authentication);
        postService.deletePost(postId, findMember);
        return new Res(new DefStatus(StatusCode.OK, "성공적으로 게시물을 삭제했습니다."),null);
    }

    /**
     * 게시물 추천
     */
    @PostMapping("/{post_id}/like")
    public Res likePost(@PathVariable("post_id") Long postId,
                         @RequestBody @Valid LikeReq postLikeReq,
                        Authentication authentication){
        Member findMember = getLoginMember(authentication);
        postService.likePost(postId, postLikeReq, findMember);

        String message = postLikeReq.getIsLike()?"추천" : "비추천";
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물을 "+message+"했습니다."));
    }

    /**
     * 게시물 신고
     */
    @PostMapping("/{post_id}/report")
    public Res reportPost(@PathVariable("post_id") Long postId,
                          @RequestBody @Valid ReportReq postReportReq,
                          Authentication authentication){
        if(!postReportReq.isValid) throw new CustomException(ErrorCode.EMPTY_DETAIL_IN_ETC_REPORT,
            "기타 카테고리 선택 시 상세 신고사유를 작성해야 합니다.");
        Member findMember = getLoginMember(authentication);
        postService.reportPost(postId, postReportReq, findMember);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물을 신고했습니다."));
    }

    /**
     * 상세 게시물 조회
     */
    @GetMapping("/{post_id}")
    public Res<PostDetailRes> readPostDetail(@PathVariable("post_id") Long postId, Authentication authentication){
        Member findMember = getLoginMember(authentication);
        PostDetailRes postDetailRes = postService.readPostDetail(postId, findMember);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물을 조회했습니다."), postDetailRes);
    }

    /**
     * 게시물 목록 조회 - 최근 10개의 게시물을 postCategory, postType, keywords 상관 없이 가져오기
     */
    @GetMapping("/home/recent")
    public Res<List<PostSimpleRes>> readsRecent10Post() {
        List<PostSimpleRes> result = postService.readsRecent10Post();

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물 목록 조회에 성공했습니다."), result);
    }

    /**
     * 게시물 목록 조회
     */
    @GetMapping
    public Res<PostsResultRes> readPosts(@RequestParam("postCategory") @Valid PostCategory postCategory,
                                         @RequestParam("postType") @Valid PostType postType,
                                         @RequestParam("sort") @Valid PostSortType postSortType,
                                         @RequestParam(required = false, name = "page", defaultValue = "0") int pageNumber,
                                         @RequestParam(required = false, name = "hasCommentCond") Boolean hasCommentCond,
                                         @RequestParam(required = false, name = "keyWordCond") String keyWordCond,
                                         @RequestParam(required = false, name = "titleCond") String titleCond,
                                         @RequestParam(required = false, name = "memberNameCond") String memberNameCond,
                                         @RequestParam(required = false, name = "isPinnedCommentCond") Boolean isPinnedCommentCond) {
        log.info("controller isPinnedCommentCond={}", isPinnedCommentCond);
        log.info("controller titleCond={}", titleCond);
        log.info("controller hasCommentCond={}", hasCommentCond);
        PostSearchDto postSearchDto = new PostSearchDto(postCategory, postType, postSortType, pageNumber, hasCommentCond, keyWordCond, titleCond, memberNameCond, isPinnedCommentCond);
        PostsResultRes postsResultRes = postService.readPosts(postSearchDto);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물 목록 조회에 성공했습니다."), postsResultRes);
    }

    /**
     * 댓글 하이라이팅
     */
    @GetMapping("/{post_id}/highlight/{comment_id}")
    public Res<PostDetailRes> readPostWithHighlightComment(@PathVariable("post_id") Long postId,
                                                           @PathVariable("comment_id") Long commentId,
                                                           Authentication authentication){
        Member findMember = getLoginMember(authentication);
        PostDetailWithHighlightCommentRes res = postService.readPostWithHighlightComment(postId, commentId, findMember);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글 하이라이팅을 포함한 게시물을 조회했습니다."), res);
    }



    private static Member getLoginMember(Authentication authentication) {
        Member loginMember = ((PrincipalDetails) authentication.getPrincipal()).getMember();
        return loginMember;
    }

}
