package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.net.URI;


@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService_v2 {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public String login(String code){
        // 토큰 받기
        String token = getToken(code);

        // 사용자 정보 받기
        getMemberInfo(token);
        // 처음 사용자라면 회원가입

        // 토큰, 사용자 정보 return
        return token;
    }

    private void getMemberInfo(String token) {

        log.info("getMemberInfo 함수 진입");
        log.info("token = {}", token);

        String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        // getForObject 메소드를 사용하여 구글 사용자 정보를 가져온다.
        ResponseEntity<String> response=restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET,request,String.class);
        log.info("memberInfo body = {}", response.getBody());
    }

    private String getToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 요청에 필요한 파라미터를 설정합니다.
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("code", code);
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        parameters.add("redirect_uri", "http://localhost:8080/oauth2callback"); // 리다이렉션 그대로 작성해야함
        //parameters.add("access_type", "offline");
        //parameters.add("approval_prompt", "force");
        parameters.add("grant_type", "authorization_code");

        // HTTP 헤더를 설정합니다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        URI uri = URI.create("https://oauth2.googleapis.com/token");


        // HTTP 요청을 보내고 OAuth2 Access Token을 받아옵니다.
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                uri, entity, String.class);
        log.info("response body={}", response.getBody());

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response.getBody());
        return jsonElement.getAsJsonObject().get("access_token").getAsString();
    }
}
