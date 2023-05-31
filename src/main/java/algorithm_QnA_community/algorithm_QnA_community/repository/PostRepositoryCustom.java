package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSearchDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.post.PostSimpleDto;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}
