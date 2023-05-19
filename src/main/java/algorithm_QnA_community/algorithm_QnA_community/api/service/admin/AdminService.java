package algorithm_QnA_community.algorithm_QnA_community.api.service.admin;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.ReportedPostDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.admin.ReportedPostsRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public ReportedPostsRes getReportedPosts(int page) {
        // 1. 신고당한 내역이 존재하는 postId 리스트 모두 가져오기
        List<Long> ReportedPostIds = reportPostRepository.findPostIdsByExist();

        // 2. post 정보들을 Pageable하게 가져오기
        Page<Post> posts = postRepository.findByPostIds(ReportedPostIds, PageRequest.of(page, 10));

        return ReportedPostsRes.builder()
                .postPage(posts)
                .build();
    }
}
