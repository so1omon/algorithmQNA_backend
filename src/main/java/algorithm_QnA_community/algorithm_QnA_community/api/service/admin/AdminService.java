package algorithm_QnA_community.algorithm_QnA_community.api.service.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.ReportedCommentDetailRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.ReportedCommentsRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.ReportedPostDetailRes;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.PostPageRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.Alarm;
import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.AlarmType;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import algorithm_QnA_community.algorithm_QnA_community.repository.*;
import algorithm_QnA_community.algorithm_QnA_community.utils.annotation.EnumValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.admin
 * fileName       : AdminService
 * author         : solmin
 * date           : 2023/05/11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/11        solmin       최초 생성
 * 2023/05/23        solmin       트랜잭션 시 조회만 하는 경우 readOnly 옵션 추가
 * 2023/05/23        solmin       관리자 서비스 구현 완료
 * 2023/05/26        solmin       댓글/글 삭제시 알람 생성 구현
 *                                TODO 추후 계층 분리해서 작성할 예정
 * 2023/06/01        solmin       공지사항 조회 API 삭제
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeCommentRepository likeCommentRepository;
    private final ReportCommentRepository reportCommentRepository;
    private final ReportPostRepository reportPostRepository;
    private final AlarmRepository alarmRepository;

    private static final int MAX_SIZE =10;
    @Transactional(readOnly = true)
    public PostPageRes getReportedPosts(int page) {
        // 1. 신고당한 내역이 존재하는 postId 리스트 모두 가져오기
        List<Long> reportedPostIds = reportPostRepository.findPostIdsByExist();

        // 2. post 정보들을 Pageable하게 가져오기
        Page<Post> posts = postRepository.findByPostIds(reportedPostIds, PageRequest.of(page, MAX_SIZE));

        return PostPageRes.builder()
                .postPage(posts)
                .build();
    }

    @Transactional(readOnly = true)
    public ReportedCommentsRes getReportedComments(int page) {

        // 1. 신고당한 내역이 존재하는 commentId 리스트 모두 가져오기
        List<Long> reportedCommentIds = reportCommentRepository.findCommentIdsByExist();

        // 2. comment 정보들을 Pageable하게 가져오기
        Page<Comment> comments = commentRepository.findByCommentIds(reportedCommentIds, PageRequest.of(page, MAX_SIZE));

        return ReportedCommentsRes.builder()
            .commentPage(comments)
            .build();
    }

    @Transactional(readOnly = true)
    public ReportedPostDetailRes getReportPostInfo(Long postId, int page) {

        Post findPost = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        Page<ReportPost> reportPostPage = reportPostRepository.findByPost(findPost, PageRequest.of(page, MAX_SIZE));

//        int totalReportedCnt = reportPostRepository.countByPost(findPost).intValue();

        return ReportedPostDetailRes.builder()
            .reportPostPage(reportPostPage)
            .post(findPost)
            .build();
    }

    @Transactional(readOnly = true)
    public ReportedCommentDetailRes getReportCommentInfo(Long commentId, int page) {
        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        Page<ReportComment> reportCommentPage = reportCommentRepository.findByComment(findComment, PageRequest.of(page, MAX_SIZE));

        return ReportedCommentDetailRes.builder()
            .reportCommentPage(reportCommentPage)
            .comment(findComment)
            .build();
    }

    @Transactional
    public void deleteReportPost(Long reportPostId) {
        reportPostRepository.deleteById(reportPostId);
    }

    @Transactional
    public void deleteReportComment(Long reportCommentId) {
        reportCommentRepository.deleteById(reportCommentId);
    }

    @Transactional
    public void deleteReportedPost(Long postId) {
        Post findPost = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        alarmRepository.save(Alarm.createAlarm()
            .member(findPost.getMember())
            .type(AlarmType.DELETE_POST)
            .msg("관리자에 의해서 게시글이 삭제되었습니다.")
            .build());

        findPost.deletePost();
        postRepository.delete(findPost);
    }

    @Transactional
    public void deleteReportedComment(Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        alarmRepository.save(Alarm.createAlarm()
            .member(findComment.getMember())
            .type(AlarmType.DELETE_COMMENT)
            .msg("관리자에 의해서 댓글이 삭제되었습니다.")
            .build());

        findComment.deleteComment();
        commentRepository.deleteById(commentId);
    }

//    @Transactional(readOnly = true)
//    public PostPageRes getNotices(int page, String category) {
//
//        PostCategory postCategory = PostCategory.valueOf(category);
//
//        return new PostPageRes(postRepository
//            .findByPostCategoryAndTypeOrderByCreatedDateDesc(postCategory, PostType.NOTICE, PageRequest.of(page, MAX_SIZE)));
//    }
//
//    @Transactional(readOnly = true)
//    public PostPageRes getNotices(int page) {
//        return new PostPageRes(postRepository
//            .findByTypeOrderByCreatedDateDesc(PostType.NOTICE,PageRequest.of(page, MAX_SIZE)));
//    }
}
