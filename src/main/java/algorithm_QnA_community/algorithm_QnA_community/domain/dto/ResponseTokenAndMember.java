package algorithm_QnA_community.algorithm_QnA_community.domain.dto;

import lombok.Data;

@Data
public class ResponseTokenAndMember {
    private String accessToken;

    private String refreshToken;
    private String memberId;
    private String memberName;

    public ResponseTokenAndMember(String accessToken, String refreshToken, String memberId, String memberName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberId = memberId;
        this.memberName = memberName;
    }
}
