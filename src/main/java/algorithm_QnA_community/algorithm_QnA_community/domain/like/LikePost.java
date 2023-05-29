package algorithm_QnA_community.algorithm_QnA_community.domain.like;

import algorithm_QnA_community.algorithm_QnA_community.domain.BaseTimeEntity;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Badge;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.utils.listner.LikeListener;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

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
 * 2023/05/10        solmin       팩토리 메소드 일부 수정
 * 2023/05/13        janguni      isDisLike 주석 처리, isLike 주석 변경
 * 2023/05/23        solmin       삭제 편의 메소드 추가
 * 2023/05/26        solmin       엔티티리스너 추가, 싫어요 시 뱃지에 영향 없도록 변경
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@EntityListeners({AuditingEntityListener.class, LikeListener.class})
public class LikePost extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "like_post_id")
    private Long id;

    @Column(nullable = false)
    private boolean isLike = true; // 추천인지 비추천인지 여부

//    @Column(nullable = false)
//    private boolean isDislike = false; // 채택된 댓글인지 여부

    @Builder(builderClassName = "createLikePost", builderMethodName = "createLikePost")
    public LikePost(Post post, Member member, boolean isLike){
        this.member = member;
        this.post = post;
        this.isLike = isLike;
        post.getLikePosts().add(this);
        post.updateLikeCnt(isLike, true);
    }

    //----------------- 연관관계 필드 시작 -----------------//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    //----------------- 연관관계 메소드 시작 -----------------//
    public void updateState(boolean isLike){
        if (isLike^this.isLike){ // 다른 상태로 변경을 시도할 때만 유효함
            // 좋아요 -> 싫어요 : -1
            updatePostWriterBadge(-1);
            this.post.updateLikeCnt(isLike, true);
            this.post.updateLikeCnt(!isLike, false);
            this.isLike = isLike;
        }
    }

    public void deleteLikePost() {
        // 좋아요 삭제 시에만 -1
        updatePostWriterBadge(-1);
        this.post = null;
        this.member = null;
    }
    @PrePersist
    public void beforeSave(){
        // 좋아요 삽입 시에만 +1
        updatePostWriterBadge(1);
    }

    private void updatePostWriterBadge(int value){
        this.post.getMember().updateMemberBadgeCnt(Badge.Like, isLike?value:0);
    }
}
