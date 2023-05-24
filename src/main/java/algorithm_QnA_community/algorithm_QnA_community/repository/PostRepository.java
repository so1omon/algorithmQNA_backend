package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : PostRepository
 * author         : solmin
 * date           : 2023/05/01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/01        solmin       최초 생성(테스트용)
 * 2023/05/16        janguni      정렬 메소드 생성
 * 2023/05/21        janguni      정렬 메소드에 페이징기능 추가
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    // 최신순으로 정렬
    Page<Post> findByCategoryAndTypeOrderByCreatedDateDesc(PostCategory category, PostType postType, Pageable pageable);

    // 오래된 순으로 정렬
    Page<Post> findByCategoryAndTypeOrderByCreatedDateAsc(PostCategory category, PostType postType, Pageable pageable);

    // 추천-비추천 내림차순
    @Query("select p from Post p" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " order by p.likeCnt-p.dislikeCnt desc")
    Page<Post> findByCategoryOrderByLike_DislikeDESC(@Param("category") PostCategory category, @Param("postType") PostType postType, Pageable pageable);

    // 추천-비추천 오름차순
    @Query("select p from Post p" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " order by p.likeCnt-p.dislikeCnt asc")
    Page<Post> findByCategoryOrderByLike_DislikeASC(@Param("category") PostCategory category, @Param("postType") PostType postType, Pageable pageable);

    // 댓글 내림차순
    @Query("select p from Post p" +
            " left join p.comments c" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " group by p order by count(c) desc")
    Page<Post> findPostOrderByCommentCntDesc(@Param("category") PostCategory category, @Param("postType") PostType postType, Pageable pageable);

    // 댓글 오름차순
    @Query("select p from Post p" +
            " left join p.comments c" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " group by p order by count(c) asc")
    Page<Post> findPostOrderByCommentCntAsc(@Param("category") PostCategory category, @Param("postType") PostType postType, Pageable pageable);

    @Query("select p from Post p" +
            " left join p.comments c" +
            " where p.category = :category" +
            " and p.type = :postType" +
            " group by p" +
            " order by p.views * 0.5 + ((p.likeCnt)*(p.likeCnt) / (p.likeCnt + p.dislikeCnt)) * 0.3 + count(c) * 0.2 desc")
    Page<Post> findByPopular(@Param("category") PostCategory category, @Param("postType") PostType postType, Pageable pageable);

    // 조회수 오름차순
    Page<Post> findByCategoryAndTypeOrderByViewsAsc(PostCategory category, PostType postType, Pageable pageable);

    // 조회수 내림차순
    Page<Post> findByCategoryAndTypeOrderByViewsDesc(PostCategory category, PostType postType, Pageable pageable);






}
