package algorithm_QnA_community.algorithm_QnA_community.config.response;

import lombok.Data;

@Data
public class MemberInfoRes {

    private String email;
    private String name;
    private String profile;

    private String state;

    public MemberInfoRes(String email, String name, String profile, String state) {
        this.email = email;
        this.name = name;
        this.profile = profile;
        this.state=state;
    }
}
