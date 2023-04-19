package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.domain.dto.ResponseTokenAndMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 로그인 (or 회원가입)
     */
    @GetMapping("/login/googles")
    public ResponseEntity<ResponseTokenAndMember> login(@RequestParam String code) {
        log.info("코드 받고 토큰과 사용자 정보 return");
        ResponseTokenAndMember responseTokenAndMember = oAuthService.login(code);
        return ResponseEntity.status(HttpStatus.OK).body(responseTokenAndMember);
    }

    @GetMapping("/oauth2callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) {
        return ResponseEntity.status(HttpStatus.OK).body(code);
    }

    @GetMapping("/test")
    public String test(){
        return "ok";
    }

}
