package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikeComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikePostRepository extends JpaRepository<LikePost, Long> {
    Optional<LikePost> findByPostIdAndMemberId(Long PostId, Long memberId);

}
