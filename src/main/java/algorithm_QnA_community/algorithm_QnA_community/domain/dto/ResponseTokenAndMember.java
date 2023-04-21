package algorithm_QnA_community.algorithm_QnA_community.domain.dto;

import algorithm_QnA_community.algorithm_QnA_community.config.response.MemberInfoRes;
import lombok.Data;

@Data
public class ResponseTokenAndMember {
    private String accessToken;

    private String refreshUUID;
    private MemberInfoRes memberInfo;

    public ResponseTokenAndMember(String accessToken, String refreshUUID, MemberInfoRes memberInfo) {
        this.accessToken = accessToken;
        this.refreshUUID = refreshUUID;
        this.memberInfo = memberInfo;
    }
}
