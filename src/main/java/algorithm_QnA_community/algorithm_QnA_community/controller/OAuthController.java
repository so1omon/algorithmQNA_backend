package algorithm_QnA_community.algorithm_QnA_community.controller;

import algorithm_QnA_community.algorithm_QnA_community.service.auth.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 회원가입
     */
//    @GetMapping("/login/google")
//    public ResponseEntity<OAuth2AccessToken> login(HttpServletRequest request) {
//        log.info("/login post방식으로 요청이 들어옴");
//        String code = request.getParameter("code");
//        OAuth2AccessToken token = oAuthService.login(code);
//
//        return ResponseEntity.status(HttpStatus.OK).body(token);
//    }


    @GetMapping("/yes")
    public String test(HttpServletRequest request, Model model){
        log.info("분명히..! 콜백함");
        String code = request.getParameter("code");
        log.info("code = {}", code);
        //OAuth2AccessToken token = oAuthService.login(code);

        //return ResponseEntity.status(HttpStatus.OK).body(token);
        return "hello";
    }

    @GetMapping("/oauth2callback")
    public String callback(@RequestParam("code") String code) {
        log.info("code={}", code);
        return "hello";
    }
}
