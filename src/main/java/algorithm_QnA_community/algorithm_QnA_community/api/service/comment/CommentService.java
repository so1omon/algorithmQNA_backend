package algorithm_QnA_community.algorithm_QnA_community.api.service.comment;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.*;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikeComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import algorithm_QnA_community.algorithm_QnA_community.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
@Slf4j
public class CommentService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeCommentRepository likeCommentRepository;
    private final ReportCommentRepository reportCommentRepository;

    // TODO 추후 SpringContextHolder 이용해서 UserCredential 받아오기
    private Member member= null;

    @Transactional
    public CommentCreateRes writeComment(Long postId, CommentCreateReq commentCreateReq){
        // TODO 추후 SpringContextHolder 이용해서 UserCredential 받아오기
        member = memberRepository.findById(1L).get();
        Comment parentComment= null;
        if(commentCreateReq.getParentCommentId()!=null) {
            parentComment = commentRepository.findById(commentCreateReq.getParentCommentId())
                .orElseThrow(()-> new EntityNotFoundException("부모 댓글이 존재하지 않습니다."));
        }

        Post post = postRepository.findById(postId)
            .orElseThrow(()-> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        Comment comment = Comment.createComment()
            .member(member)
            .post(post)
            .content(commentCreateReq.getContent())
            .parent(parentComment)
            .build();

        commentRepository.save(comment);

        return new CommentCreateRes(comment);
    }

    @Transactional
    public void updateComment(Long commentId, String content) {
        // TODO 추후 SpringContextHolder 이용해서 UserCredential 받아오기
        Comment comment = checkAuthoritiesAndGetComment(commentId);

        comment.updateContent(content);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = checkAuthoritiesAndGetComment(commentId);

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public Res updateLikeInfo(Long commentId, @Valid CommentLikeReq commentLikeReq) {
        member = memberRepository.findById(2L).get();
        Comment comment = commentRepository.findByIdWithMember(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));
        Optional<LikeComment> likeCommentInfo = likeCommentRepository.findByCommentIdAndMemberId(commentId, member.getId());

        if(commentLikeReq.getCancel()){ // 만약 나의 추천정보 삭제라면
            try{
                if(likeCommentInfo.isPresent()) {
                    comment.updateLikeCnt(likeCommentInfo.get().isLike(), false);
                }
                likeCommentRepository.deleteByCommentIdAndMemberId(commentId, member.getId());
            }catch (EmptyResultDataAccessException e){
                log.info("존재하지 않는 추천정보 삭제 시도");
            }

            return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 추천정보를 삭제했습니다."));
        }else{
            // 1. 만약 존재한다면 업데이트하고 끝 (updateState)
            if(likeCommentInfo.isPresent()){
                likeCommentInfo.get().updateState(commentLikeReq.getIsLike());
                log.info("댓글 추천정보 업데이트");
            }else{// 2. 존재하지 않는다면 새로 생성
                LikeComment likeComment = LikeComment.createLikeComment()
                    .isLike(commentLikeReq.getIsLike())
                    .member(member)
                    .comment(comment)
                    .build();

                likeCommentRepository.save(likeComment);
                log.info("댓글 추천정보 생성");
            }
            String message = commentLikeReq.getIsLike()?"추천" : "비추천";
            return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 "+message+"했습니다."));
        }
    }

    @Transactional
    public CommentResultRes pinComment(Long commentId) {
        member = memberRepository.findById(1L).get();
        Comment comment = commentRepository.findByIdWithPost(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if(comment.getPost().getMember().getId()!=member.getId()){
            throw new CustomException(ErrorCode.UNAUTHORIZED, "댓글을 채택할 수 있는 권한이 존재하지 않습니다.");
        }
        if(comment.isPinned()){
            throw new CustomException(ErrorCode.DUPLICATED_TASK, "이미 채택된 댓글입니다.");
        }

        List<Comment> comments = commentRepository.findByPostIdAndPinned(comment.getPost().getId());
        comments.forEach(c->{
            c.updatePin(false);
        });
        comment.updatePin(true);

        return CommentResultRes.builder()
            .commentMemberId(comment.getMember().getId())
            .commentMemberName(comment.getMember().getName())
            .updatedAt(comment.lastModifiedDate)
            .commentId(commentId)
            .build();
    }

    @Transactional
    public CommentResultRes reportComment(Long commentId, CommentReportReq commentReportReq) {
        member = memberRepository.findById(1L).get();
        Comment comment = commentRepository.findByIdWithMember(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if(comment.getMember().getId()==member.getId()) {
            throw new CustomException(ErrorCode.REPORT_MY_RESOURCE, "자신이 작성한 댓글은 신고할 수 없습니다.");
        }

        Optional<ReportComment> reportComment = reportCommentRepository.findByCommentIdAndMemberId(commentId, member.getId());
        if(!reportComment.isPresent()){
            ReportComment newReportComment = ReportComment.createReportComment()
                .comment(comment)
                .member(member)
                .category(ReportCategory.valueOf(commentReportReq.getCategory()))
                .detail(commentReportReq.getDetail())
                .build();

            reportCommentRepository.save(newReportComment);
            return new CommentResultRes(commentId, member.getId(), member.getName(), newReportComment.lastModifiedDate);
        }

        reportComment.get().updateReportInfo(
            ReportCategory.valueOf(commentReportReq.getCategory()),
            commentReportReq.getDetail()
        );

        return new CommentResultRes(commentId, member.getId(), member.getName(), reportComment.get().lastModifiedDate);
    }

    private Comment checkAuthoritiesAndGetComment(Long commentId) {
        member = memberRepository.findById(1L).get();

        // 1. comment_id에 해당하는 comment 객체 가져와서, member와 동일한 사람인지 검증
        Comment comment = commentRepository.findByIdWithMember(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if(comment.getMember()!=member){
            throw new CustomException(ErrorCode.UNAUTHORIZED, "댓글을 수정할 수 있는 권한이 존재하지 않습니다.");
        }
        return comment;
    }


}
