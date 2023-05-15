package algorithm_QnA_community.algorithm_QnA_community.api.service.admin;

import algorithm_QnA_community.algorithm_QnA_community.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void test() {

        List<Long> postIdsByExist = reportPostRepository.findPostIdsByExist();
        for (Long aLong : postIdsByExist) {
            log.info("{}", aLong);
        }
    }
}
