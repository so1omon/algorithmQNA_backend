package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config.auth
 * fileNmae         : CustomOAuth2UserService
 * author           : janguni
 * date             : 2023-05-02
 * description      : 구글 로그인 테스트 용도 (삭제예정)
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/04/20       janguni         최초 생성
 * 2023/05/11       solmin          DOCS 수정
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 액세스 토큰을 사용하여 사용자 정보를 가져온다.
        String accessToken = userRequest.getAccessToken().getTokenValue();

        //DefaultOAuth2User 서비스를 통해 User 정보를 가져와야 하기 때문에 대리자 생성
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String id = oAuth2User.getName();
        log.info("id={}", id);
        String email = oAuth2User.getAttribute("email");
        log.info("email={}", email);
        String profile = oAuth2User.getAttribute("picture");
        log.info("profile={}", profile);

        return oAuth2User;

    }

}
