package algorithm_QnA_community.algorithm_QnA_community.api.service.comment;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.LikeReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.ReportReq;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.*;
import algorithm_QnA_community.algorithm_QnA_community.api.service.s3.S3Service;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.Alarm;
import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.AlarmType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

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
 * 2023/05/11        solmin       PR 리뷰내용 전부 반영
 * 2023/05/15        solmin       controller 단에서 authentication 받아서 로그인한 유저 검증
 * 2023/05/16        solmin       페이지를 이용한 댓글 리스트 조회 및 일부 댓글 조회 로직 구현
 * 2023/05/18        janguni      CommentLikeReq -> LikeReq, CommentReportReq -> ReportReq로 변경
 * 2023/05/19        solmin       나의 반응 리스트를 미리 조회해서 CommetRes 생성 시 나의 반응정보 같이 삽입
 * 2023/05/19        solmin       공통 DTO 참조 변경
 * 2023/05/23        solmin       일부 @Transactional readonly 추가
 * 2023/05/26        solmin       댓글 작성, 채택 시 관련된 멤버에게 알림 생성
 *                                TODO 추후 계층 분리해서 작성할 예정
 * 2023/06/01        solmin       댓글 작성 시 임시 경로에 존재하는 이미지 정보 삭제
 * 2023/06/02        solmin       댓글 조회 페이징 형태로 변경
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
    private final AlarmRepository alarmRepository;
    private final S3Service s3Service;

    @Transactional
    public CommentCreateRes writeComment(Long postId, CommentCreateReq commentCreateReq, Member member){

        Comment parentComment= null;
        Long parentCommentId = commentCreateReq.getParentCommentId();

        if(parentCommentId !=null) {
            parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(()-> new EntityNotFoundException("부모 댓글이 존재하지 않습니다."));
        }

        Post findPost = postRepository.findById(postId)
            .orElseThrow(()-> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        Comment comment = Comment.createComment()
            .member(member)
            .post(findPost)
            .content(commentCreateReq.getContent())
            .parent(parentComment)
            .build();

        Comment savedComment = commentRepository.save(comment);

        savedComment.updateContent(
            s3Service.moveImages(savedComment.getId(),
                savedComment.getContent(),
                commentCreateReq.getImageIds(),
                S3Service.COMMENT_DIR));

        Long savedCommentId = savedComment.getId();

        String message  = member.getName()+ "님이 댓글을 남겼습니다.";

        List<Member> membersToNotify = new ArrayList<>();
        // 1. 게시글 작성자에게
        if(comment.getPost().getMember().getId()!=member.getId()) {
            membersToNotify.add(comment.getPost().getMember());
        }
        // 2. 내 부모 댓글에게
        if(comment.getParent()!=null && comment.getParent().getMember().getId()!= member.getId()){
            membersToNotify.add(comment.getParent().getMember());
        }
        // 3. mentioner에게
        if(comment.getMentioner()!=null && comment.getMentioner().getId()!= member.getId()){
            membersToNotify.add(comment.getMentioner());
        }
        membersToNotify.forEach(targetMember -> {
            alarmRepository.save(Alarm.createAlarm()
                .member(targetMember)
                .type(AlarmType.COMMENT_WRITE)
                .msg(message)
                .subjectMemberName(member.getName())
                .member(targetMember)
                .commentId(savedCommentId)
                .eventUrl("/post/"+comment.getPost().getId())
                .build());
        });

        return new CommentCreateRes(comment);
    }

    @Transactional
    public void updateComment(Long commentId, String content, Member member) {
        Comment comment = checkAuthoritiesAndGetComment(commentId, member);

        comment.updateContent(content);
    }

    @Transactional
    public void deleteComment(Long commentId, Member member) {
        Comment comment = checkAuthoritiesAndGetComment(commentId, member);

        comment.deleteComment();

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public Res updateLikeInfo(Long commentId, @Valid LikeReq commentLikeReq, Member member) {
        Comment findComment = commentRepository.findByIdWithMember(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));
        Optional<LikeComment> findLikeComment = likeCommentRepository.findByCommentIdAndMemberId(commentId, member.getId());

        if(commentLikeReq.getCancel() && findLikeComment.isPresent()){ // 만약 나의 추천정보 삭제라면
            findComment.updateLikeCnt(findLikeComment.get().isLike(), false);
            findLikeComment.get().deleteLikeComment();
            likeCommentRepository.deleteById(findLikeComment.get().getId());
            return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 추천정보를 삭제했습니다."));
        }else{
            // 1. 만약 존재한다면 업데이트하고 끝 (updateState)
            if(findLikeComment.isPresent()){
                findLikeComment.get().updateState(commentLikeReq.getIsLike());
                log.info("댓글 추천정보 업데이트");
            }else{// 2. 존재하지 않는다면 새로 생성
                LikeComment likeComment = LikeComment.createLikeComment()
                    .isLike(commentLikeReq.getIsLike())
                    .member(member)
                    .comment(findComment)
                    .build();

                likeCommentRepository.save(likeComment);
                log.info("댓글 추천정보 생성");
            }
            String message = commentLikeReq.getIsLike()?"추천" : "비추천";
            return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 댓글을 "+message+"했습니다."));
        }
    }

    @Transactional
    public void pinComment(Long commentId, Member member) {

        Comment findComment = commentRepository.findByIdWithPost(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if(findComment.getPost().getMember().getId()!=member.getId()){
            throw new CustomException(ErrorCode.UNAUTHORIZED, "댓글을 채택할 수 있는 권한이 존재하지 않습니다.");
        }
        if(findComment.isPinned()){
            throw new CustomException(ErrorCode.DUPLICATED_TASK, "이미 채택된 댓글입니다.");
        }

        List<Comment> comments = commentRepository.findByPostIdAndPinned(findComment.getPost().getId());
        comments.forEach(c->{
            c.updatePin(false);
        });
        findComment.updatePin(true);

        // 채택 시 알람 생성
        if(member.getId()!=findComment.getMember().getId()) {
            alarmRepository.save(Alarm.createAlarm()
                .member(findComment.getMember())
                .type(AlarmType.PINNED)
                .msg(member.getName() + "님이 댓글을 채택했습니다.")
                .eventUrl("/post/" + findComment.getPost().getId())
                .subjectMemberName(member.getName())
                .build());
        }

    }

    @Transactional
    public void reportComment(Long commentId, ReportReq commentReportReq, Member member) {

        Comment findComment = commentRepository.findByIdWithMember(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if(findComment.getMember().getId()==member.getId()) {
            throw new CustomException(ErrorCode.REPORT_MY_RESOURCE, "자신이 작성한 댓글은 신고할 수 없습니다.");
        }

        Optional<ReportComment> findReportComment = reportCommentRepository.findByCommentIdAndMemberId(commentId, member.getId());
        if(!findReportComment.isPresent()){
            ReportComment reportComment = ReportComment.createReportComment()
                .comment(findComment)
                .member(member)
                .reportCategory(ReportCategory.valueOf(commentReportReq.getCategory()))
                .detail(commentReportReq.getDetail())
                .build();
            reportCommentRepository.save(reportComment);
        }else{
            findReportComment.get().updateReportInfo(
                ReportCategory.valueOf(commentReportReq.getCategory()),
                commentReportReq.getDetail()
            );
        }
    }

    @Transactional(readOnly = true)
    public CommentsRes getComments(Long postId, int page, Long memberId) {
        // 0. 게시글 조회 (없으면 404)
        Post findPost = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        // 0-1. 내가 좋아요 누른 댓글들 목록을 Map으로 구성
        // TODO 추후 outer join을 통해 최적화된 DTO로 구성하기 (현재는 full scan)

        // key : commentId, value : isLike
        Map<Long, Boolean> myLikeInfoMap = likeCommentRepository.findByMemberId(memberId).stream()
            .collect(toMap(lc->lc.getComment().getId(), lc->lc.isLike()));

        // 1. page 내의 최상위 댓글들 가져오기
        Page<Comment> commentsByPost = commentRepository.findCommentsByPostAndDepth(findPost,0, PageRequest.of(page, 10));

        // 2. 최상위 댓글들의 아이디 목록 가져오기
        Map<Long, TopCommentRes> topCommentMap = new LinkedHashMap<>();

        for(Comment comment : commentsByPost){
            topCommentMap.put(comment.getId(), new TopCommentRes(comment, myLikeInfoMap.getOrDefault(comment.getId(), null)));
        }

        List<Long> commentIds = topCommentMap.keySet().stream().collect(Collectors.toList());

        // 3. 최상위 댓글들의 자식 댓글들까지 가져오기 (최상위 댓글당 최대 10개까지, 생성일 기준)
        // TODO 모든 댓글 작성자 Id를 set에 넣고 영속성 컨텍스트 초기화
        Map<Long, CommentRes> childCommentMap = new LinkedHashMap<>();

        for(Comment comment : commentRepository.findTop10ByParent(commentIds)){
            childCommentMap.put(comment.getId(), new CommentRes(comment, myLikeInfoMap.getOrDefault(comment.getId(), null)));
        }

        // 4. 자식 댓글들 Id 리스트를 보내고 이중 자식 댓글을 갖고 있는 댓글 ID만 map에 hasChild update
        for(Long id : commentRepository.existsChildByParentIds(new ArrayList<>(childCommentMap.keySet()))){
            childCommentMap.get(id).setHasChild(true);
        }

        childCommentMap.values().forEach(commentRes -> {
            topCommentMap.get(commentRes.getParentId()).addChild(commentRes);
        });

        return CommentsRes.builder()
            .postId(postId)
            .commentPage(commentsByPost)
            .commentList(topCommentMap.values().stream()
                .map(t->t.updatePageInfo(commentRepository.countByParentIdAndDepth(t.getCommentId(),1).intValue()))
                .collect(Collectors.toList()))
            .build();
    }

    @Transactional(readOnly = true)
    public MoreCommentListRes getMoreCommentsByParent(Long parentCommentId, int page, Long memberId) {
        // 0. 게시글 조회 (없으면 404)
        Comment parentComment = commentRepository.findById(parentCommentId)
            .orElseThrow(() -> new EntityNotFoundException("부모 댓글이 존재하지 않습니다."));
        // 0-1. 내가 좋아요 누른 댓글들 목록을 Map으로 구성
        // TODO 추후 outer join을 통해 최적화된 DTO로 구성하기 (현재는 full scan)

        // key : commentId, value : isLike
        Map<Long, Boolean> myLikeInfoMap = likeCommentRepository.findByMemberId(memberId).stream()
            .collect(toMap(lc->lc.getComment().getId(), lc->lc.isLike()));

        // TODO 위와 마찬가지로 모든 댓글 작성자 Id를 set에 넣고 영속성 컨텍스트 초기화
        // 1. 부모 댓글 id에 해당하는 자식 댓글 리스트(depth=2)를 page에 따라서 가져오기
        Page<Comment> commentsByPost = commentRepository.findCommentsByParent(parentComment, PageRequest.of(page, 10));
        Map<Long, CommentRes> commentMap = new LinkedHashMap<>();

        for(Comment comment : commentsByPost){
            commentMap.put(comment.getId(), new CommentRes(comment, myLikeInfoMap.getOrDefault(comment.getId(), null)));
        }

        for(Long id : commentRepository.existsChildByParentIds(new ArrayList<>(commentMap.keySet()))){
            commentMap.get(id).setHasChild(true);
        }


        return new MoreCommentListRes(parentCommentId, commentMap.values().stream().collect(Collectors.toList()), commentsByPost );
    }

    private Comment checkAuthoritiesAndGetComment(Long commentId, Member member) {
//        member = memberRepository.findById(1L).get();

        // 1. comment_id에 해당하는 comment 객체 가져와서, member와 동일한 사람인지 검증
        Comment findComment = commentRepository.findByIdWithMember(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if(findComment.getMember().getId()!=member.getId()){
            throw new CustomException(ErrorCode.UNAUTHORIZED, "댓글을 수정할 수 있는 권한이 존재하지 않습니다.");
        }
        return findComment;
    }
}

