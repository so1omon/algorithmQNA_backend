package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.Announce;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnnounceRepository extends JpaRepository<Announce,Long> {
}
