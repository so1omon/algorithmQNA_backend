package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : ReportCommentRepository
 * author         : solmin
 * date           : 2023/05/09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/09        solmin       최초 생성
 * 2023/05/18        solmin       generic 수정
 */
public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {
    @Query("select distinct (rp.post.id) from ReportPost rp")
    List<Long> findPostIdsByExist();

    Optional<ReportPost> findByPostIdAndMemberId(Long postId, Long memberId);
}
