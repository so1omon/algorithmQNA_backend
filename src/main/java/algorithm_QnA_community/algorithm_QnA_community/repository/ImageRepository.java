package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : ImageRepository
 * author         : solmin
 * date           : 2023/06/01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/06/01        solmin       최초 생성
 */
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByCreatedDateBefore(LocalDateTime stdTime);

    void deleteByUrl(String url);
}
