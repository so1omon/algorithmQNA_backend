package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.Token;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthService_v2 oAuthService;

    /**
     * 로그인 (or 회원가입)
     */
    @GetMapping("/login/googles")
    public ResponseEntity<String> login(@RequestParam String code) {
        log.info("/login post방식으로 요청이 들어옴");
        String token = oAuthService.login(code);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @GetMapping("/oauth2callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) {
        String token = oAuthService.login(code);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

}
