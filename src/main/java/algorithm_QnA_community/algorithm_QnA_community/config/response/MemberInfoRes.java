package algorithm_QnA_community.algorithm_QnA_community.config.response;

import lombok.Data;

@Data
public class MemberInfoRes {
    private String id;
    private String name;
    private String profile;

    public MemberInfoRes(String id, String name, String profile) {
        this.id = id;
        this.name = name;
        this.profile = profile;
    }
}
