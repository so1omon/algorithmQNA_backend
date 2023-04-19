package algorithm_QnA_community.algorithm_QnA_community.domain.dto;

import lombok.Data;

@Data
public class AccessAndRefreshToken {
    private String accessToken;
    private String refreshToken;

    public AccessAndRefreshToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
