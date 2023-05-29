package algorithm_QnA_community.algorithm_QnA_community.api.controller.member;

import algorithm_QnA_community.algorithm_QnA_community.api.service.member.MemberService;
import algorithm_QnA_community.algorithm_QnA_community.api.service.s3.S3Service;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.PrincipalDetails;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

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
@Slf4j
@Validated
public class MemberApiController {
    private final S3Service s3Service;
    private final MemberService memberService;

    @GetMapping
    public Res<MemberDetailDto> getMember(Authentication authentication){
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 내 정보를 조회했습니다."),
            new MemberDetailDto(getLoginMember(authentication)));
    }

    @PatchMapping
    public Res updateMemberName(@RequestBody @Valid MemberNameReq memberNameReq, Authentication authentication){
        Member loginMember = ((PrincipalDetails) authentication.getPrincipal()).getMember();
        memberService.updateMemberName(loginMember, memberNameReq.getMemberName());

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 닉네임을 변경했습니다."));
    }

    @PostMapping("/profile")
    public Res<MemberProfileDto> updateMemberProfile(@RequestParam("file") MultipartFile file,
                                                     Authentication authentication) {
        Member loginMember = ((PrincipalDetails) authentication.getPrincipal()).getMember();

        MemberProfileDto result = s3Service.updateMemberProfile(loginMember, file);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 프로필을 변경했습니다."),
            result);
    }

    private static Member getLoginMember(Authentication authentication) {
        Member loginMember = ((PrincipalDetails) authentication.getPrincipal()).getMember();
        return loginMember;
    }
}
