package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.domain.response.ResponseMessage;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.MemberInfoRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.StatusCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.CodeAndState;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.dto.ResponseTokenAndMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
 * 2023/05/02       janguni         failTokenAuthentication() 생성
 * 2023/05/03       janguni         deleteCookie() 생성
 * 2023/05/18       janguni         redirectToGoogle() 추가
 * 2023/05/19       janguni         쿠키 키값 변경
 */

@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;
    @Value("${cookie.domain}")
    private String domain;

    @Value("${cookie.domain}")
    private String domain;


    /**
     * 구글 로그인 페이지로 리다이렉트
     */
    @GetMapping("/oauth/google")
    public String redirectToGoogle(){
        return "redirect:" + oAuthService.getOauthRedirectURL();
    }

    /**
     * 로그인 또는 회원가입
     * @param code (인증코드)
     *         state (상태값)
     */
    @GetMapping("/oauth/login")
    public ResponseEntity<Res> login(@RequestParam String code, @RequestParam String state) {
        log.info("======= 로그인 시도=======");
        // 인증코드로 액세스 토큰, refreshUUID, 멤버정보 불러옴
        ResponseTokenAndMember responseTokenAndMember = oAuthService.login(code, state);


        if (responseTokenAndMember == null) {
            log.info("로그인에 실패하여 결국 forbidden");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Res(new DefStatus(StatusCode.FORBIDDEN, ResponseMessage.FAIL_AUTHORIZE_CODE), null));
        }

        else {
            MemberInfoRes memberInfo = responseTokenAndMember.getMemberInfo();

            ResponseCookie accessCookie = ResponseCookie.from("access_token", responseTokenAndMember.getAccessToken())
                    .httpOnly(true)
                    .path("/")
                    .domain(domain)
                    //.secure(true)
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refresh_uuid", responseTokenAndMember.getRefreshUUID())
                    .httpOnly(true)
                    //.secure(true)
                    .domain(domain)
                    .path("/")
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                    .body(new Res(new DefStatus(StatusCode.OK, ResponseMessage.SUCCESS_AUTHORIZE), memberInfo));
        }
    }

    /**
     * 토큰 인증 실패 시
     */
    @GetMapping("/oauth/not-secured")
    public ResponseEntity<Res> failTokenAuthentication() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Res(new DefStatus(StatusCode.FORBIDDEN, ResponseMessage.EXPIRATION_TOKENS),null));
    }

    /**
     * 쿠키 삭제 요청
     */
    @GetMapping("/oauth/deleteCookie")
    public ResponseEntity<Res> deleteCookie(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie accessCookie = new Cookie("access_token", "");
            accessCookie.setMaxAge(0);

            Cookie refreshCookie = new Cookie("refresh_uuid", "");
            refreshCookie.setMaxAge(0);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Res(new DefStatus(StatusCode.OK, ResponseMessage.SUCCESS_DELETE_COOKIE),null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Res(new DefStatus(StatusCode.BAD_REQUEST, ResponseMessage.FAIL_DELETE_COOKIE),null));
        }
    }



    // ====================== 임시용 ====================== //
//    @GetMapping("/google/callback")
//    public ResponseEntity<CodeAndState> callback(@RequestParam String code, @RequestParam String state){
//        CodeAndState codeAndState = new CodeAndState(code, state);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(codeAndState);
//    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@AuthenticationPrincipal PrincipalDetails principal){
        log.info("email={}", principal.getMember().getEmail());
        return ResponseEntity.status(HttpStatus.OK)
                .body("성공!!");
    }
}
