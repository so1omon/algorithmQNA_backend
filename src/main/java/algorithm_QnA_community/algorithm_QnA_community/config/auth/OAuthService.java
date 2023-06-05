package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.MemberInfoRes;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.dto.AccessTokenAndRefreshUUID;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.dto.ResponseTokenAndMember;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config.auth
 * fileNmae         : OAuthService
 * author           : janguni
 * date             : 2023-05-03
 * description      : 구글 Oauth2 server로 부터 token 발급, 사용자 정보 반환
 *
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/04/20       janguni         최초 생성
 * 2023/05/18       janguni         getOauthRedirectURL() 추가
 */


@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    private final JwtTokenProvider tokenProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${google.uri}")
    private String googleLoginUri;


    /**
     * 구글 로그인 창으로 이동하는 uri 구성
     */
    public String getOauthRedirectURL() {

        Map<String, Object> params = new HashMap<>();
        params.put("scope", "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile");
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);
        params.put("access_type", "offline");
        params.put("approval_prompt", "force");
        params.put("state", UUID.randomUUID().toString());

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return googleLoginUri + "?" + parameterString;
    }



    public ResponseTokenAndMember login(String code, String state){
        log.info("      code={}", code);
        log.info("      state={}", state);

        AccessTokenAndRefreshUUID tokenInfo = getToken(code); // 액세스 토큰과 refreshUUID 얻어옴

        if (tokenInfo==null) return null; // 잘못된 인증코드로 인해 토큰을 받아오지 못함

        MemberInfoRes memberInfo = getMemberInfo(tokenInfo.getAccessToken(), state); // 사용자 정보 받기


        // 처음 로그인을 시도한 사용자라면 회원가입 처리
        Optional<Member> findMember = memberRepository.findByEmail(memberInfo.getEmail());
        if (findMember.isEmpty()){
            log.info("처음 들어온 회원={}", memberInfo.getName());
            Member member = Member.createMember()
                    .email(memberInfo.getEmail())
                    .name(memberInfo.getName())
                    .role(Role.ROLE_USER)
                    .profileImgUrl(memberInfo.getProfile())
                    .build();
            memberRepository.save(member);
        }

        // jwt 토큰 발급
        log.info("jwt 토큰 발급 직전");
        String token = tokenProvider.createToken(memberInfo.getEmail(), Role.ROLE_USER.value());
        log.info("token={}", token);
        log.info("jwt 토큰 발급 직후");

        ResponseTokenAndMember tokenAndMember = new ResponseTokenAndMember(token, token, memberInfo);
        return tokenAndMember;
    }

    // refresh token으로 accessToken 재발급
    public String sendTokens(String refreshUUID){

        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String refreshToken = vop.get(refreshUUID);
        log.info("refreshToken={}", refreshToken);
        if (refreshToken==null) {
            return null;
        }

        URI uri = URI.create("https://oauth2.googleapis.com/token");

        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        parameters.add("refresh_token", refreshToken);
        parameters.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);


        try {
            ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.getBody());
            String newAccessToken = jsonElement.getAsJsonObject().get("access_token").getAsString();
            return newAccessToken;
        } catch (Exception e) {
            log.info("google에서 재발급을 안함");
            return null;
        }
    }

    public MemberInfoRes getMemberInfo(String accessToken, String state) {
        try {
            String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

            //header에 accessToken을 담는다.
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);


            //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

            // getForObject 메소드를 사용하여 구글 사용자 정보를 가져온다.
            ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
            log.info("구글로 부터 받아온");
            log.info("  memberInfo body = {}", response.getBody());

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.getBody());
            String email = jsonElement.getAsJsonObject().get("email").getAsString();
            String name = jsonElement.getAsJsonObject().get("name").getAsString();
            String profile = jsonElement.getAsJsonObject().get("picture").getAsString();

            MemberInfoRes memberInfoRes = new MemberInfoRes(email, name, profile, state);
            log.info("memberInfoRes={}", memberInfoRes);
            return memberInfoRes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 인증코드로 토큰 정보 return
     * @param code
     * @return accessToken refreshUUID
     */
    private AccessTokenAndRefreshUUID getToken(String code) {

        // HTTP 요청에 필요한 파라미터를 설정
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("code", code);
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        parameters.add("redirect_uri", redirectUri);
        parameters.add("access_type", "offline");
        parameters.add("approval_prompt", "force");
        parameters.add("grant_type", "authorization_code");

        // HTTP 헤더를 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //URI uri = URI.create("https://accounts.google.com/o/oauth2/token");
        URI uri = URI.create("https://oauth2.googleapis.com/token");

        // HTTP 요청을 보내고 OAuth2 Access Token을 받아옴
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);
        ResponseEntity<String> response;

        try {
            response = restTemplate.postForEntity(uri, entity, String.class);
        } catch (Exception e) {
            log.error("유효하지 않은 code로 인해 토큰발급받지 못함");
            return null;
        }

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response.getBody());
        String accessToken = jsonElement.getAsJsonObject().get("access_token").getAsString();
        String refreshToken = jsonElement.getAsJsonObject().get("refresh_token").getAsString();

        log.info("google로 부터 받아옴");
        log.info("  access_token= {}", accessToken);
        log.info("  refresh_token={}", refreshToken);

        // refreshToken redis에 저장(uuid가 key)
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String uuid = UUID.randomUUID().toString();

        vop.set(uuid, refreshToken); // redis에 저장

        AccessTokenAndRefreshUUID accessAndRefreshToken = new AccessTokenAndRefreshUUID(accessToken, uuid);
        return accessAndRefreshToken;
    }
}
