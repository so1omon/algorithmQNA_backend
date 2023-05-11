package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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
    // 단일 댓글 삭제
    void deleteById(Long commentId);

    // 멤버 페치조인
    @Query("select c from Comment c " +
        " join fetch c.member m" +
        " where c.id = :comment_id")
    Optional<Comment> findByIdWithMember(@Param("comment_id") Long commentId);

    // 게시글 페치조인
    @Query("select c from Comment c " +
        " join fetch c.post p" +
        " where c.id = :comment_id")
    Optional<Comment> findByIdWithPost(@Param("comment_id") Long commentId);

    // 게시글 내의 모든 댓글 가져오기 (게시글은 영속 X)
    @Query("select c from Comment c " +
        " left join c.post p" +
        " where p.id = :post_id")
    List<Comment> findByPostId(@Param("post_id") Long postId);

    // 게시글의 고정된 댓글 가져오기
    // 혹시라도 두개 이상 pin이 되었을 가능성을 없애기 위해서 List로 받아옴
    @Query("select c from Comment c " +
        " left join c.post p" +
        " where p.id = :post_id" +
        " and c.isPinned = true")
    List<Comment> findByPostIdAndPinned(@Param("post_id") Long postId);

}
