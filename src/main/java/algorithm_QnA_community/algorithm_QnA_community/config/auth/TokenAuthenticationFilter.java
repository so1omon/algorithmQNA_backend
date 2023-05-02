package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

// 모든 요청에 accesstoken과 refreshUUID가 담기는 지 확인 - 0
// accessToken만료 -> 재발급해서 보내주기 - 0
// refreshUUID도 만료 -> 403 forbidden 에러
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter implements InitializingBean {

    private final OAuthService oAuthService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, RuntimeException{
        String accessToken = request.getHeader("access_token");
        String refreshUUID = request.getHeader("refreshUUID");

        log.info("------------------------------------------------");
        log.info("accessToken -> {}", accessToken);
        log.info("refreshUUID -> {}", refreshUUID);
        log.info("------------------------------------------------");

        if (accessToken != null & refreshUUID != null) {
            log.info("access_token과 refreshUUID 둘 다 있음");
            URL url = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            log.info("responseCode={}", responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { // accessToken 유효
                log.info("accessToken이 유효함");
                createAuthentication();
            }

            else {
                log.info("accessToken이 유효하지 않음");
                String accessToken_new = oAuthService.sendTokens(refreshUUID);
                log.info("accessToken_new={}", accessToken_new);

                if (accessToken_new!= null) {
                    log.info("refresh로 재발급 성공");
                    createAuthentication();
                }
                else{
                    log.info("refresh로 재발급 실패");
                    throw new RuntimeException();
                }
            }
        }
        else {
            log.info("토큰 둘 다 없음");
            throw new RuntimeException();
        }
        filterChain.doFilter(request, response);
    }

    private void createAuthentication() {

        PrincipalDetails principalDetails = new PrincipalDetails();
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
