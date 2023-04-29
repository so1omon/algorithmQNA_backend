package algorithm_QnA_community.algorithm_QnA_community.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Role role;

    private int commentBadgeCnt;
    private int postBadgeCnt;
    private int likeBadgeCnt;
    private String profileImgUrl;

    @Builder(builderClassName = "createMember", builderMethodName = "createMember")
    public Member(String name, String email, Role role, String profileImgUrl) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImgUrl = profileImgUrl;
    }
}
