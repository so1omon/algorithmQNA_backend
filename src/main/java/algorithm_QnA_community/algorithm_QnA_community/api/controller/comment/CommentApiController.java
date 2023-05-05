package algorithm_QnA_community.algorithm_QnA_community.api.controller.comment;

import algorithm_QnA_community.algorithm_QnA_community.api.service.comment.CommentService;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
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
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentApiController {
    private final CommentService commentService;

    @Transactional
    @PostMapping("/{post_id}")
    public CommentCreateRes writeComment(@PathVariable("post_id") Long postId,
                                         @RequestBody CommentCreateReq commentCreateReq){
        log.info("postId : {}", postId);
        log.info("content : {}", commentCreateReq.getContent());
        log.info("parentCommentId : {}", commentCreateReq.getParentCommentId());
        CommentCreateRes result= commentService.writeComment(postId, commentCreateReq);

        return result;
    }




}
