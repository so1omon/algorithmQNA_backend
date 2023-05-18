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
 * 2023/05/15        solmin       mentioner 필드 추가 및 Builder 수정
 * 2023/05/16        solmin       삭제 편의 연관 관계 메소드 추가, 추후 다음 링크 참고해서 튜닝할 것
 *                                https://www.inflearn.com/questions/39769/%EB%B6%80%EB%AA%A8-%EC%9E%90%EC%8B%9D%EA%B4%80%EA%B3%84%EC%97%90%EC%84%9C-%EB%B6%80%EB%AA%A8-%EC%82%AD%EC%A0%9C%EC%8B%9C-set-null%EB%B0%A9%EB%B2%95%EC%97%90-%EB%8C%80%ED%95%B4%EA%B6%81%EA%B8%88%ED%95%A9%EB%8B%88%EB%8B%A4
 *
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

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> child = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioner_id")
    private Member mentioner;

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
            if(parent.getDepth()>=2){ // parent의 depth>=2인 상황
                this.mentioner = parent.getMember();
                this.depth = parent.getDepth();
                this.parent = parent.getParent();
                this.parent.getParent().getChild().add(this);
            }else{
                this.parent = parent;
                this.depth = parent.getDepth()+1;
                parent.getChild().add(this);
            }
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

    // TODO 추후 배치 update JPQL(객체지향 쿼리 언어2 - 벌크 연산 참고)을 사용해서
    // 한번에 해당 부모와 관련있는 모든 자식 엔티티의 부모 FK 값을 null로 변경

    public void deleteComment(){
        for(Comment childComment : child){
            childComment.deleteComment();
        }
        this.parent = null;
        this.member = null;
        this.post = null;
    }
}
