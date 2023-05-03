package algorithm_QnA_community.algorithm_QnA_community.domain.post;

import algorithm_QnA_community.algorithm_QnA_community.domain.BaseTimeEntity;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikePost;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.report.ReportPost;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.domain
 * fileName       : Post
 * author         : solmin
 * date           : 2023/04/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/04/26        solmin       최초 생성
 * 2023/05/01        solmin       불필요한 setter 삭제 및 일부 Validation 추가
 *                                TEXT->LONGTEXT
 * 2023/05/02        solmin       조회수 기능 추가 -> 추후 쿠키를 이용해서 중복 피할 예정
 *                                LONGTEXT -> TEXT로 변경 (요구사항이 default page size = 16K를 초과하지 않음)
 *                                추가로 XSS 방지를 위해 스크립트를 HTML 엔티티로 인코딩 이후 조회 시 디코딩하는 작업 필요
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank
    private String content;

    private int likeCnt;

    private int dislikeCnt;

    private int views;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @Builder(builderClassName = "createPost", builderMethodName = "createPost")
    public Post(Member member, String title, String content, PostCategory category){
        this.member = member;
        member.getPosts().add(this);
        this.title = title;
        this.content = content;
        this.category = category;
    }

    //----------------- 연관관계 필드 시작 -----------------//

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<LikePost> likePosts = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<ReportPost> reportPosts = new ArrayList<>();



    //----------------- 연관관계 메소드 시작 -----------------//

    public void updateLikeCnt(boolean isLike, boolean isIncrement){
        if(isLike){
            likeCnt = isIncrement? likeCnt+1 : likeCnt-1;
        }else{
            dislikeCnt = isIncrement? dislikeCnt+1 : dislikeCnt-1;
        }
    }


}
