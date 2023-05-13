package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {
    Optional<ReportPost> findByPostIdAndMemberId(Long postId, Long memberId);
}
