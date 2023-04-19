package algorithm_QnA_community.algorithm_QnA_community.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    private String id;

    private String name;

    private ROLE role;

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Member(String id, String name, ROLE role) {
        this.id = id;
        this.name = name;
        this.role = ROLE.USER;
    }
}
