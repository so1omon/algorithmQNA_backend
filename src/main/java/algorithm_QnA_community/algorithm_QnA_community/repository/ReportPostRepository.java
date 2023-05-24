package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
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
 * 2023/05/18        solmin       generic 수정
 * 2023/05/23        solmin       페이징 메소드 추가
 */
public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {
    @Query("select distinct (rp.post.id) from ReportPost rp")
    List<Long> findPostIdsByExist();

    Optional<ReportPost> findByPostIdAndMemberId(Long postId, Long memberId);

    Page<ReportPost> findByPost(Post post, Pageable pageable);
}
