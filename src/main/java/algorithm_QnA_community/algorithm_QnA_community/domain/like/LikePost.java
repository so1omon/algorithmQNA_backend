package algorithm_QnA_community.algorithm_QnA_community.domain.like;

import algorithm_QnA_community.algorithm_QnA_community.domain.BaseTimeEntity;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import lombok.*;

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
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class LikePost extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "like_post_id")
    private Long id;

    @Column(nullable = false)
    private boolean isLike = true; // 채택된 댓글인지 여부

    @Column(nullable = false)
    private boolean isDislike = false; // 채택된 댓글인지 여부

    @Builder(builderClassName = "createLikePost", builderMethodName = "createLikePost")
    public LikePost(Post post, Member member, boolean isLike){
        this.member = member;
        this.post = post;
        this.isLike = isLike;
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
            this.post.updateLikeCnt(isLike, true);
            this.post.updateLikeCnt(!isLike, false);
            this.isLike = isLike;
        }
    }
}
