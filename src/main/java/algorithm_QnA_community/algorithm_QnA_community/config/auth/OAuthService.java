package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.config.response.MemberInfoRes;
import algorithm_QnA_community.algorithm_QnA_community.domain.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.Role;
import algorithm_QnA_community.algorithm_QnA_community.domain.dto.AccessTokenAndRefreshUUID;
import algorithm_QnA_community.algorithm_QnA_community.domain.dto.ResponseTokenAndMember;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    private final RedisTemplate redisTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;


    public ResponseTokenAndMember login(String code, String state){

        AccessTokenAndRefreshUUID tokenInfo = getToken(code); // 액세스 토큰과 refreshUUID 얻어옴

        if (tokenInfo==null) return null; // 잘못된 인증코드로 인해 토큰을 받아오지 못함

        MemberInfoRes memberInfo = getMemberInfo(tokenInfo.getAccessToken(), state); // 사용자 정보 받기

        // 처음 로그인을 시도한 사용자라면 회원가입 처리
        Optional<Member> findMember = memberRepository.findByEmail(memberInfo.getName());
        if (findMember.isEmpty()){
            Member member = Member.createMember()
                    .email(memberInfo.getEmail())
                    .name(memberInfo.getName())
                    .role(Role.ROLE_USER)
                    .profileImgUrl(memberInfo.getProfile())
                    .build();
            memberRepository.save(member);

        }

        ResponseTokenAndMember tokenAndMember = new ResponseTokenAndMember(tokenInfo.getAccessToken(), tokenInfo.getRefreshUUID(), memberInfo);
        return tokenAndMember;
    }

    // refresh token으로 accessToken 재발급
    public String sendTokens(String refreshUUID){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String refreshToken = vop.get(refreshUUID);

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
            return null;
        }
    }

    private MemberInfoRes getMemberInfo(String accessToken, String state) {

        log.info("accessToken = {}", accessToken);

        String GOOGLE_USERINFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

        //header에 accessToken을 담는다.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);


        //HttpEntity를 하나 생성해 헤더를 담아서 restTemplate으로 구글과 통신하게 된다.
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        // getForObject 메소드를 사용하여 구글 사용자 정보를 가져온다.
        ResponseEntity<String> response=restTemplate.exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET,request,String.class);
        log.info("memberInfo body = {}", response.getBody());

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response.getBody());
        String email = jsonElement.getAsJsonObject().get("email").getAsString();
        String name = jsonElement.getAsJsonObject().get("name").getAsString();
        String profile = jsonElement.getAsJsonObject().get("picture").getAsString();

        MemberInfoRes memberInfoRes = new MemberInfoRes(email, name, profile, state);
        return memberInfoRes;
    }

    /**
     * 인증코드로 토큰 정보 return
     * @param code
     * @return accessToken refreshUUID
     */
    private AccessTokenAndRefreshUUID getToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

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
        URI uri = URI.create("https://accounts.google.com/o/oauth2/token");

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

        // refreshToken redis에 저장(uuid가 key)
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String uuid = UUID.randomUUID().toString();

        vop.set(uuid, refreshToken); // redis에 저장

        AccessTokenAndRefreshUUID accessAndRefreshToken = new AccessTokenAndRefreshUUID(accessToken, uuid);
        return accessAndRefreshToken;
    }
}
