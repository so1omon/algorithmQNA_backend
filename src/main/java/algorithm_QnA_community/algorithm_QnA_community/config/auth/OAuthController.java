package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.config.response.MemberInfoRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.dto.ResponseTokenAndMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 로그인 (or 회원가입)
     */
    @GetMapping("/oauth2/token/new")
    public ResponseEntity<MemberInfoRes> login(@RequestParam String code) {
        log.info("코드 받고 토큰과 사용자 정보 return");
        ResponseTokenAndMember responseTokenAndMember = oAuthService.login(code);

        if (responseTokenAndMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }

        else {
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
                    .body(responseTokenAndMember.getMemberInfo());
        }
    }


    /**
     * access token 재발급
     * @param refreshUUID
     * @return
     */
    @GetMapping("/oauth2/token/renew")
    public ResponseEntity<String> sendTokens(@CookieValue String refreshUUID){
        log.info("refreshUUID={}", refreshUUID);
        String accessToken = oAuthService.sendTokens(refreshUUID);
        if (accessToken.equals("invalid_UUID")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("invalid_UUID");
        } else if (accessToken.equals("invalid_refreshToken")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("invalid_refreshToken");
        }

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), accessCookie.toString())
                .body(null);
    }

    @GetMapping("/test")
    public String test(){
        return "ok";
    }

}
