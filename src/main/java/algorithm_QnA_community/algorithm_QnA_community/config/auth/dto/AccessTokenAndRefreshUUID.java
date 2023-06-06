package algorithm_QnA_community.algorithm_QnA_community.config.auth.dto;

import lombok.Data;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.domain.dto
 * fileNmae         : AccessTokenAndRefreshUUID
 * author           : janguni
 * date             : 2023-05-02
 * description      : accessToken과 refreshUUID DTO
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/04/20       janguni         최초 생성
 */

@Data
public class AccessTokenAndRefreshUUID {
    private String accessToken;
    private String refreshUUID;

    public AccessTokenAndRefreshUUID(String accessToken, String refreshUUID) {
        this.accessToken = accessToken;
        this.refreshUUID = refreshUUID;
    }
}
