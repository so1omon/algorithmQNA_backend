package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : CommentRepository
 * author         : solmin
 * date           : 2023/05/04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/04        solmin       최초 생성
 */

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
