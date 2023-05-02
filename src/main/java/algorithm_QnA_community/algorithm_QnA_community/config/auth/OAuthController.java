package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.config.ResponseMessage;
import algorithm_QnA_community.algorithm_QnA_community.config.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.config.response.MemberInfoRes;
import algorithm_QnA_community.algorithm_QnA_community.config.response.Res;
import algorithm_QnA_community.algorithm_QnA_community.config.response.StatusCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.dto.CodeAndState;
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

    // 임시
    @GetMapping("/getcode")
    public String getcode(){
        return "redirect:/oauth2/authorize/google";
    }

    // 임시
    @GetMapping("/google/callback")
    public ResponseEntity<CodeAndState> callback(@RequestParam String code, @RequestParam String state){
        CodeAndState codeAndState = new CodeAndState(code, state);
        return ResponseEntity.status(HttpStatus.OK)
                .body(codeAndState);
    }

    //test용
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.status(HttpStatus.OK)
                .body("성공!!");
    }

    @GetMapping("/auth/not-secured")
    public ResponseEntity<Res> notSecured() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Res(new DefStatus(StatusCode.FORBIDDEN, ResponseMessage.EXPIRATION_TOKENS),"not_secured"));
    }

    @GetMapping("/auth/denied")
    public ResponseEntity<Res> denied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Res(new DefStatus(StatusCode.FORBIDDEN, ResponseMessage.EXPIRATION_TOKENS),"denied"));
    }



   /**
     * 로그인 또는 회원가입
     * @param code (인증코드)
     * @return
     */
    @GetMapping("/login")
    public ResponseEntity<Res> login(@RequestParam String code, @RequestParam String state) {

        // 인증코드로 액세스 토큰, refreshUUID, 멤버정보 불러옴
        ResponseTokenAndMember responseTokenAndMember = oAuthService.login(code, state);


        if (responseTokenAndMember == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Res(new DefStatus(StatusCode.FORBIDDEN, ResponseMessage.FAIL_AUTHORIZE), null));
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
     * access token 재발급
     * @param refreshUUID
     * @return
     */
    @GetMapping("/oauth2/token/renew")
    public ResponseEntity<String> sendTokens(@RequestParam String refreshUUID){
        log.info("access 토큰 재발급");
        log.info("refreshUUID={}", refreshUUID);
        String accessToken = oAuthService.sendTokens(refreshUUID);

        if (accessToken.equals("invalid_UUID")){ // refreshUUID가 잘못되었을 때
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("invalid_UUID");
        } else if (accessToken.equals("invalid_refreshToken")) { // refreshToken이 유효하지 않을 때
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("invalid_refreshToken");
        }

        // accessToken 쿠키에 담아서 리턴
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), accessCookie.toString())
                .body(null);
    }

}
