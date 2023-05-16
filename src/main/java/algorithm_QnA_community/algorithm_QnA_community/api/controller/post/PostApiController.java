package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentCreateReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentCreateRes;
import algorithm_QnA_community.algorithm_QnA_community.api.service.post.PostService;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.PrincipalDetails;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * 2023/05/11        janguni       최초 생성
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
    public void writePost(@RequestBody @Valid PostCreateReq postCreateReq, PrincipalDetails principal){
        //Long memberId = principal.getMember().getId();
        postService.writePost(postCreateReq, 1L);
    }

    /**
     * 게시물 수정
     */
    @PatchMapping("/{post_id}")
    public void updatePost(@PathVariable("post_id") Long postId,
                           @RequestBody @Valid PostCreateReq postCreateReq, PrincipalDetails principal){
        //Long memberId = principal.getMember().getId();
        postService.updatePost(postId,postCreateReq, 1L);
    }

    /**
     * 게시물 삭제
     */
    @DeleteMapping("/{post_id}")
    public void deletePost(@PathVariable("post_id") Long postId, PrincipalDetails principal){
        //Long memberId = principal.getMember().getId();
        postService.deletePost(postId, 1L);
    }

    /**
     * 게시물 추천
     */
    @PostMapping("/{post_id}/like")
    public Res likePost(@PathVariable("post_id") Long postId,
                         @RequestBody @Valid PostLikeReq postLikeReq,
                         PrincipalDetails principal){
        //Long memberId = principal.getMember().getId();

        String message = postLikeReq.getIsLike()?"추천" : "비추천";
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물을 "+message+"했습니다."));
    }

    /**
     * 게시물 신고
     */
    @PostMapping("/{post_id}/report")
    public Res reportPost(@PathVariable("post_id") Long postId,
                          @RequestBody @Valid PostReportReq postReportReq,
                          PrincipalDetails principal){

        //Long memberId = principal.getMember().getId();
        postService.reportPost(postId, postReportReq, 1L);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물을 신고했습니다."));
    }

    /**
     * 게시물 조회
     */
    @GetMapping("/{post_id}")
    public Res<PostDetailRes> readPost(@PathVariable("post_id") Long postId,
                          PrincipalDetails principal){

        //Long memberId = principal.getMember().getId();
        PostDetailRes postDetailRes = postService.readPost(postId, 1L);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 게시물을 조회했습니다."),postDetailRes);
    }

}
