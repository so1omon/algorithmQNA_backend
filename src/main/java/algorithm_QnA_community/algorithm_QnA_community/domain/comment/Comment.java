package algorithm_QnA_community.algorithm_QnA_community.domain.comment;

import algorithm_QnA_community.algorithm_QnA_community.domain.BaseTimeEntity;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikeComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportComment;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain
 * fileName       : Comment
 * author         : solmin
 * date           : 2023/04/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        solmin       최초 생성
 * 2023/05/01        solmin       content NotBlank 추가
 * 2023/05/05        solmin       팩토리 메소드 오류 수정
 * 2023/05/10        solmin       댓글 정보 수정 및 채택을 위한 업데이트 메소드 추가
 *                                TODO 추후 Validation Error 테스트 시마다 검증 어노테이션 메세지 추가 예정
 * 2023/05/11        solmin       DynamicInsert, DynamicUpdate 추가
 */
@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@DynamicInsert // RequestDto에 특정 필드가 빈 값으로 들어오는 상황에서 insert query에 null을 넣지 않고 값이 삽입되는 필드만 set
@DynamicUpdate // RequestDto에 특정 필드가 빈 빈 값으로 들어오는 상황에서 update query에 null을 넣지 않고 변경된 필드만 set
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private int depth;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "게시글 내용을 1글자 이상 작성해야 합니다.")
    private String content;

    private boolean isPinned = false; // 채택된 댓글인지 여부

    private int likeCnt;

    private int dislikeCnt;

    //----------------- 연관관계 필드 시작 -----------------//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> child = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "comment")
    private List<LikeComment> likeComments = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    private List<ReportComment> reportComments = new ArrayList<>();

    //----------------- 연관관계 메소드 시작 -----------------//

    @Builder(builderClassName = "createComment", builderMethodName = "createComment")
    private Comment(Post post, Comment parent, Member member, String content){
        this.post = post;
        this.parent = parent;
        this.member = member;
        this.content = content;
        member.getComments().add(this);
        if(parent!=null){
            this.depth = parent.getDepth()+1;
            parent.getChild().add(this);
        }
    }

    public void updateLikeCnt(boolean isLike, boolean isIncrement){
        if(isLike){
            likeCnt = isIncrement? likeCnt+1 : likeCnt-1;
        }else{
            dislikeCnt = isIncrement? dislikeCnt+1 : dislikeCnt-1;
        }
    }

    public void updateContent(String content){
        this.content = content;
    }

    public void updatePin(boolean isPinned){
        this.isPinned = isPinned;
    }

}
