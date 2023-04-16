//package algorithm_QnA_community.algorithm_QnA_community.config.auth;
//
//
//import algorithm_QnA_community.algorithm_QnA_community.domain.Member;
//import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class OAuthService {
//    private final MemberRepository memberRepository;
//    private OAuth2AuthorizedClientService authorizedService;
//
//
//    @Transactional
//    public OAuth2AccessToken login(String code) {
//
//        // accessToken, refreshToken 생성
//        //authorizedService.
//        OAuth2RefreshToken refreshToken = tokenProvider.getRefreshToken(code);
//
//        // accessToken을 통해 사용자 정보 불러오기
//        Member member= getMemberInfo(accessToken);
//        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
//        if (findMember.isEmpty()) {
//            Member savedMember = memberRepository.save(findMember.get());
//            TokenMember tokenMember = new TokenMember(accessToken, savedMember);
//            return accessToken;
//        }
//
//        return accessToken;
//    }
//
//    private Member getMemberInfo(OAuth2AccessToken accessToken) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + accessToken.getValue());
//            HttpEntity<String> entity = new HttpEntity<>("body", headers);
//            ResponseEntity<String> response = tokenProvider.getOAuth2ResTemplate().exchange(
//                    "https://www.googleapis.com/oauth2/v2/userinfo",
//                    HttpMethod.GET,
//                    entity,
//                    String.class);
//            String responseBody = response.getBody();
//            ObjectMapper objectMapper = new ObjectMapper();
//            try {
//                // JSON 파싱하여 Member 객체 생성
//                Member member = objectMapper.readValue(responseBody, Member.class);
//                return member;
//
//            } catch (JsonProcessingException e) {
//                log.error("Failed to parse user info JSON: {}", responseBody);
//                throw new RuntimeException("Failed to parse user info JSON", e);
//            }
//        }
//        catch (Exception e) {
//            log.error("Failed to get user info", e);
//            throw new RuntimeException("Failed to get user info", e);
//        }
//    }
//
//
//
//}