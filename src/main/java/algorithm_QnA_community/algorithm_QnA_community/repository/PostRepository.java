package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
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
 * 2023/05/22        solmin       findByPostIds <- AdminService에서 사용중
 * 2023/06/01        janguni      PostRepositoryCutom 상속 추가
 * 2023/06/26        solmin       게시글 특성 구분없이 최근 10개의 목록 가져오는 API 추가
 */

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // postId 리스트에 해당하는 post를 Pageable하게 가져오기
    // AdminService에서 사용중입니다!
    @Query(value = "select p from Post p where p.id in :postIds")
    Page<Post> findByPostIds(@Param("postIds") List<Long> postIds, Pageable pageable);

    Page<Post> findByTypeOrderByCreatedDateDesc(PostType postType, Pageable pageable);

    Page<Post> findByMemberOrderByCreatedDateDesc(Member member, Pageable pageable);

    List<Post> findTop10ByOrderByCreatedDateDesc();
}
