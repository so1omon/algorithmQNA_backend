package algorithm_QnA_community.algorithm_QnA_community.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain
 * fileName       : BaseEntity
 * author         : solmin
 * date           : 2023/04/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        solmin       최초 생성, 추후 createDate를 사용하지 않거나 시간정보가 필요하지 않은 Entity는
 *                                상속 없이 별도로 구현할 예정
 */

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
    @CreatedDate
    @Column(name="created_at")
    public LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name="updated_at")
    public LocalDateTime lastModifiedDate;
}
