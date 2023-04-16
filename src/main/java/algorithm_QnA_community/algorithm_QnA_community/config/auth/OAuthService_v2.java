package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.token.Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService_v2 {

    private final MemberRepository memberRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public String login(String code){
        return getToken(code);
    }

    private String getToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 요청에 필요한 파라미터를 설정합니다.
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("code", code);
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        parameters.add("redirect_uri", "http://localhost:8080/oauth2callback"); // 리다이렉션 그대로 작성해야함
        parameters.add("grant_type", "authorization_code");
        log.info("redirectUri={}", redirectUri);

        // HTTP 헤더를 설정합니다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        URI uri = URI.create("https://oauth2.googleapis.com/token");


        // HTTP 요청을 보내고 OAuth2 Access Token을 받아옵니다.
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                uri, entity, String.class);
        return response.getBody();

    }
}
