package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.domain.response.ResponseMessage;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.MemberInfoRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.StatusCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.CodeAndState;
import algorithm_QnA_community.algorithm_QnA_community.domain.dto.ResponseTokenAndMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config.auth
 * fileNmae         : OAuthController
 * author           : janguni
 * date             : 2023-05-02
 * description      : 로그인, 토큰 인증 처리 controller
 *                     - /login
 *                          인증코드로 토큰 정보, 사용자 정보 반환
 *                     - /auth/not-secured
 *                          토큰 인증 실패 시 내부 리다이렉션 경로
 *
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/04/20       janguni         최초 생성
 * 2023/05-02       janguni         /auth/not-secured 경로 추가
 */

@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 로그인 또는 회원가입
     * @param code (인증코드)
     *         state (상태값)
     */
    @GetMapping("/login")
    public ResponseEntity<Res> login(@RequestParam String code, @RequestParam String state) {

        // 인증코드로 액세스 토큰, refreshUUID, 멤버정보 불러옴
        ResponseTokenAndMember responseTokenAndMember = oAuthService.login(code, state);


        if (responseTokenAndMember == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Res(new DefStatus(StatusCode.FORBIDDEN, ResponseMessage.FAIL_AUTHORIZE_CODE), null));
        }

        else {
            MemberInfoRes memberInfo = responseTokenAndMember.getMemberInfo();

            ResponseCookie accessCookie = ResponseCookie.from("accessToken", responseTokenAndMember.getAccessToken())
                    .httpOnly(true)
                    .secure(true)
                    .build();


            ResponseCookie refreshCookie = ResponseCookie.from("refreshUUID", responseTokenAndMember.getRefreshUUID())
                    .httpOnly(true)
                    .secure(true)
                    .build();


            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                    .body(new Res(new DefStatus(StatusCode.OK, ResponseMessage.SUCCESS_AUTHORIZE), memberInfo));
        }
    }

    /**
     * 토큰 인증 실패 시
     * @return
     */
    @GetMapping("/auth/not-secured")
    public ResponseEntity<Res> notSecured() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Res(new DefStatus(StatusCode.FORBIDDEN, ResponseMessage.EXPIRATION_TOKENS),null));
    }




    // ====================== 임시용 ====================== //
    @GetMapping("/google/callback")
    public ResponseEntity<CodeAndState> callback(@RequestParam String code, @RequestParam String state){
        CodeAndState codeAndState = new CodeAndState(code, state);
        return ResponseEntity.status(HttpStatus.OK)
                .body(codeAndState);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.status(HttpStatus.OK)
                .body("성공!!");
    }

}
