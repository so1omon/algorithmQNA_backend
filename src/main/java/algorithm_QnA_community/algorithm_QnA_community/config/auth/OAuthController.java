package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.domain.dto.ResponseTokenAndMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 로그인 (or 회원가입)
     */
    @GetMapping("/login/googles")
    public ResponseEntity<String> login(@RequestParam String code) {
        log.info("코드 받고 토큰과 사용자 정보 return");
        ResponseTokenAndMember responseTokenAndMember = oAuthService.login(code);
        ResponseCookie accessCookie = ResponseCookie.from("accessToken",responseTokenAndMember.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshUUID",responseTokenAndMember.getRefreshUUID())
                .httpOnly(true)
                .secure(true)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(responseTokenAndMember.getMemberName());
    }

    /**
     * 인증코드 반환
     * @param code
     * @return
     */
    @GetMapping("/oauth2callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) {
        return ResponseEntity.status(HttpStatus.OK).body(code);
    }

    /**
     * access token 재발급
     * @param refreshUUID
     * @returnr
     */
    @GetMapping("/sendTokens")
    public ResponseEntity<String> sendTokens(@CookieValue String refreshUUID){
        String accessToken = oAuthService.sendTokens(refreshUUID);
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    @GetMapping("/test")
    public String test(){
        return "ok";
    }

}
