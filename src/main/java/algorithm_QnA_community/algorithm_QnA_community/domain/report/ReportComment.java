package algorithm_QnA_community.algorithm_QnA_community.domain.report;

import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain
 * fileName       : LikeComment
 * author         : solmin
 * date           : 2023/04/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        solmin       최초 생성
 * 2023/05/01        solmin       DynamicInsert및 update 추가, 일부 Validation 변경,
 *                                update method 통합
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@DynamicInsert // RequestDto에 빈 값으로 들어오는 상황에서 null로 update하지 않고 기본값으로 insert
@DynamicUpdate // RequestDto에 빈 값으로 들어오는 상황에서 null로 update하지 않고 기본값으로 update
@EntityListeners(AuditingEntityListener.class)
public class ReportComment {
    @Id
    @GeneratedValue
    @Column(name = "report_comment_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    @ColumnDefault("'기타 사유 없음'")
    @Column(length = 1000)
    private String detail;

    @LastModifiedDate
    @Column(name="updated_at")
    public LocalDateTime lastModifiedDate;

    @Builder(builderClassName = "createReportComment", builderMethodName = "createReportComment")
    public ReportComment(Comment comment, Member member,
                         ReportCategory category, String detail){
        this.member = member;
        this.comment = comment;
        this.category = category;
        this.detail = detail;
    }

    public void updateReportInfo(@NonNull ReportCategory category, String detail){
        this.detail = detail;
        this.category = category;
    }

    //----------------- 연관관계 필드 시작 -----------------//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
