package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * 2023/05/23        solmin       일부 인터페이스 추가
 */
public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {
    @Query("select distinct (rp.comment.id) from ReportComment rp")
    List<Long> findCommentIdsByExist();
    Optional<ReportComment> findByCommentIdAndMemberId(Long commentId, Long memberId);
    Page<ReportComment> findByComment(Comment comment, Pageable pageable);
}
