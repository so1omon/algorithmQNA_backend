package algorithm_QnA_community.algorithm_QnA_community.api.controller.member;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.comment.CommentApiController;
import algorithm_QnA_community.algorithm_QnA_community.api.service.member.MemberService;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.PrincipalDetails;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.member
 * fileName       : MemberApiController
 * author         : solmin
 * date           : 2023/05/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/25        solmin       최초 생성, 의존성 필요한 부분 제외한 단순 유저 정보 조회 API
 */


@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping
    public Res<MemberDetailDto> getMember(Authentication authentication){
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 내 정보를 조회했습니다."),
            new MemberDetailDto(getLoginMember(authentication)));
    }


    private static Member getLoginMember(Authentication authentication) {
        Member loginMember = ((PrincipalDetails) authentication.getPrincipal()).getMember();
        return loginMember;
    }
}
