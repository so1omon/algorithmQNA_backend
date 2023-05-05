package algorithm_QnA_community.algorithm_QnA_community.api.service.comment;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentCreateReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentCreateRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.repository.CommentRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import algorithm_QnA_community.algorithm_QnA_community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityNotFoundException;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.comment
 * fileName       : CommentService
 * author         : solmin
 * date           : 2023/05/04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/04        solmin       최초 생성
 */

@Service
@RequiredArgsConstructor
public class CommentService {
    // 추후 SpringContextHolder 이용해서 UserCredential 받아오기
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentCreateRes writeComment(Long postId, CommentCreateReq commentCreateReq){
        Member member = memberRepository.findById(1L).get();

        Comment parentComment= null;
        if(commentCreateReq.getParentCommentId()!=null) {
            parentComment = commentRepository.findById(commentCreateReq.getParentCommentId()).orElse(null);
        }

        Post post = postRepository.findById(postId).orElseThrow(()-> new EntityNotFoundException());

        Comment comment = Comment.createComment()
            .member(member)
            .post(post)
            .content(commentCreateReq.getContent())
            .parent(parentComment)
            .build();

        commentRepository.save(comment);

        return new CommentCreateRes(comment);
    }


}
