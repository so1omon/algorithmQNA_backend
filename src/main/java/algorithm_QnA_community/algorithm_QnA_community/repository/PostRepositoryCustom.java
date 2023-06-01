package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSearchDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSimpleDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : postRepositoryCustom
 * author         : janguni
 * date           : 2023/05/31
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/31        janguni           최초 생성
 */
public interface PostRepositoryCustom {

    // 최신순
    Page<PostSimpleDto> findPostsOrderByCreatedDateDesc(PostSearchDto postSearchDto, Pageable pageable);

    // 오래된순
    Page<PostSimpleDto> findPostsOrderByCreatedDateAsc(PostSearchDto postSearchDto, Pageable pageable);

    // 추천-비추천 내림차순
    Page<PostSimpleDto> findPostsOrderByLikeDesc(PostSearchDto postSearchDto, Pageable pageable);

    // 추천-비추천 오름차순
    Page<PostSimpleDto> findPostsOrderByLikeAsc(PostSearchDto postSearchDto, Pageable pageable);


    // 댓글 내림차순
    Page<PostSimpleDto> findPostsOrderByCommentSizeDesc(PostSearchDto postSearchDto, Pageable pageable);


    // 댓글 오름차순
    Page<PostSimpleDto> findPostsOrderByCommentSizeAsc(PostSearchDto postSearchDto, Pageable pageable);


    // 조회수 오름차순
    Page<PostSimpleDto> findPostsOrderByViewAsc(PostSearchDto postSearchDto, Pageable pageable);


    // 조회수 내림차순
    Page<PostSimpleDto> findPostsOrderByViewDesc(PostSearchDto postSearchDto, Pageable pageable);


    // 인기순
    Page<PostSimpleDto> findPostsOrderByPopularDesc(PostSearchDto postSearchDto, Pageable pageable);

}
