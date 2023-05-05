package algorithm_QnA_community.algorithm_QnA_community.domain.response;

import lombok.Data;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.response
 * fileNmae         : MemberInfoRes
 * author           : janguni
 * date             : 2023-05-02
 * description      : /login 성공 시 반환 DTO
 *
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/04/20       janguni         최초 생성
 */

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
