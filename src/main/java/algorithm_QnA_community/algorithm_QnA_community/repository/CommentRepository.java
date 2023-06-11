package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentWithIsLikeDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

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
 * 2023/05/16        solmin       댓글 삭제 메소드 삭제 (JPA 기본구현)
 * 2023/05/16        solmin       서비스단에 필요한 인터페이스 메소드 구현
 * 2023/06/01        solmin       findByMemberOrderByCreatedDateDesc 추가
 * 2023/06/11       janguni      findChildCommentPageNumberByParentCommentId, findCommentWithLikedByParentId 추가
 */

public interface CommentRepository extends JpaRepository<Comment, Long> {

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


    // 최상단 댓글 가져옴
    List<Comment> findTop10ByPostIdAndDepthEqualsOrderByCreatedDateDesc(Long postId, int depth);


    // 게시글 내의 최상위 댓글10개 가져오기
    @Query(nativeQuery = true,
            value = "select c.* from comment c" +
                    " where c.parent_id = :parent_id" +
                    " order by c.created_at desc" +
                    " Limit 10")
    List<Comment> findCommentsByParentIdLimit10(@Param("parent_id") Long parentId);

    List<Comment> findTop10ByParentIdAndDepthEqualsOrderByCreatedDateDesc(Long parentId, int depth);

    Long countByParentIdAndDepth(Long parentId, int depth);

//    @Query("select c from Comment c " +
//        " left join c.post p" +
//        " where p.id = :post_id" +
//        " and c.depth=0 order by c.createdDate")
    Page<Comment> findCommentsByPostAndDepth(Post post, int depth, Pageable pageable);

    @Query(value = "SELECT * FROM ( " +
        " SELECT *, RANK() OVER (PARTITION BY c.parent_id order by c.created_at) AS RN " +
        " FROM comment as c " +
        " where c.parent_id in (:parentIds)" +
        " ) AS RANKING" +
        " WHERE RANKING.RN <= 10",
    nativeQuery = true)
    List<Comment> findTop10ByParent(@Param("parentIds") List<Long> parentIds);

    // depth=1인 댓글들 중 자식 댓글이 존재하면 해당 댓글 아이디를 리턴
    @Query(value = "select distinct(c.parent_id)" +
        " from comment as c" +
        " where c.parent_id in (:parentIds)",  nativeQuery = true)
    List<Long> existsChildByParentIds(@Param("parentIds") List<Long> parentIds);


//    @Query("select c from Comment c " +
//        " where c.parent.id=:parent_id" +
//        " order by c.createdDate")
    Page<Comment> findCommentsByParent(Comment parent, Pageable pageable);

    @Query("select c from Comment c where c.post.id in :postIds")
    List<Comment> findByPoster(@Param("postIds") List<Long> postIds);
    // 게시글 내의 댓글 갯수 구하기
    @Query("select count(c) from Comment c " +
            " left join c.post p" +
            " where p.id = :postId")
    int countCommentByPostId(@Param("postId") Long postId);


    @Query(value = "select c from Comment c where c.id in :commentIds")
    Page<Comment> findByCommentIds(@Param("commentIds") List<Long> commentIds, Pageable pageable);
    Page<Comment> findByMemberOrderByCreatedDateDesc(Member member, Pageable pageable);


    // 자식 댓글 id의 row number 구하기
    @Query(value = "select count(c) from Comment c where c.id >= :commentId and c.parent.id = :parentCommentId")
    int findChildCommentPageNumberByParentCommentId(@Param("commentId") Long commentId, @Param("parentCommentId") Long parentId);

    @Query(value = "select count(c) from Comment c where c.id >= :commentId and c.depth=0")
    int findCommentPageNumberByCommentId(@Param("commentId") Long commentId);


    // 해당 페이지의 최상위 댓글 정보 가져오기 (사용자의 추천정보도 함께)
    @Query(value = "select new algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentWithIsLikeDto(c, lc.isLike)"+
                    " from Comment c" +
                    " left join LikeComment lc on lc.comment.id = c.id and lc.member.id = :memberId" +
                    " where c.depth = 0 and c.post.id = :postId" +
                    " order by c.id desc")
    Page<CommentWithIsLikeDto> findTopCommentWithIsLikeDto(@Param("memberId") Long memberId,
                                                           @Param("postId") Long postId,
                                                           Pageable pageable);

    // 해당 페이지의 대댓글 정보 가져오기 (사용자의 추천정보도 함께)
    @Query(value = "select new algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentWithIsLikeDto(c, lc.isLike)"+
            " from Comment c" +
            " left join LikeComment lc on lc.comment.id = c.id and lc.member.id = :memberId" +
            " where c.parent.id = :parentId and c.post.id = :postId" +
            " order by c.id desc")
    Page<CommentWithIsLikeDto> findChildCommentWithIsLikeDto(@Param("memberId") Long memberId,
                                                           @Param("postId") Long postId,
                                                           @Param("parentId") Long parentId,
                                                           Pageable pageable);




}
