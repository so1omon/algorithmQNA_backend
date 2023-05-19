package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.service.post.PostService;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.PrincipalDetails;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostApiController {

    private final PostService postService;

    /**
     * 게시물 등록
     */
    @PostMapping("/")
    public Res writePost(@RequestBody @Valid PostCreateReq postCreateReq, Authentication authentication){
        Member findMember = getLoginMember(authentication);
        postService.writePost(postCreateReq, findMember);
        return new Res(new DefStatus(StatusCode.CREATED, "성공적으로 게시물을 등록했습니다."),null);
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
     * 게시물 목록 조회
     */
    @GetMapping("")
    public Res<PostsResultRes> readPosts(@RequestParam("categoryName") @Valid PostCategory categoryName,
                                         @RequestParam("type") @Valid PostType type,
                                         @RequestParam("sort") @Valid PostSortType sortName,
                                         @RequestParam("page") int pageNumber){
        PostsResultRes postsResultRes = postService.readPosts(categoryName, sortName, pageNumber);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물 목록 조회에 성공했습니다."),postsResultRes);
    }

    private static Member getLoginMember(Authentication authentication) {
        Member loginMember = ((PrincipalDetails) authentication.getPrincipal()).getMember();
        return loginMember;
    }

}
