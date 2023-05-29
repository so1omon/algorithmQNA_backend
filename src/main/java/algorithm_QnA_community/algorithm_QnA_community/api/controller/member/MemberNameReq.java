package algorithm_QnA_community.algorithm_QnA_community.api.controller.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.member
 * fileName       : MemberNameReq
 * author         : solmin
 * date           : 2023/05/29
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/29        solmin       최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberNameReq {

    @NotBlank(message = "멤버 이름을 공백을 제외하고 2글자 이상 20글자 이하로 작성하세요.")
    @Size(min=2, max=20, message = "멤버 이름을 공백을 제외하고 2글자 이상 20글자 이하로 작성하세요.")
    private String memberName;
}