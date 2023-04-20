package algorithm_QnA_community.algorithm_QnA_community.domain.dto;

import lombok.Data;

@Data
public class AccessTokenAndRefreshUUID {
    private String accessToken;
    private String refreshUUID;

    public AccessTokenAndRefreshUUID(String accessToken, String refreshUUID) {
        this.accessToken = accessToken;
        this.refreshUUID = refreshUUID;
    }
}
