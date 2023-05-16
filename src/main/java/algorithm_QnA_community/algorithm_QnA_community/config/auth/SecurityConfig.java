package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config.auth
 * fileNmae         : SecurityConfig
 * author           : janguni
 * date             : 2023-05-02
 * description      :
 *                      - 필터 순서
 *                        (1) tokenAuthenticationFilter
 *                                 - 토큰 검증
 *                        (2) ExceptionHandlerFilter
 *                                 - (1) 과정에서 발생 한 예외처리
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/05/02       janguni         최초 생성
 * 2023/05/10        solmin         인증기능 연동 전까지만 comment 열어두겠습니다...,
 * 2023/05/15        solmin         OSIV - OpenEntityManagerInterceptor의 유저객체 영속상태를 이때부터
 *                                  유지시키기 위해서 filter로 교체 후 우선순위를 높임
 *                                  OpenEntityManagerInView가 DelegatingFilterProxy보다 먼저 작동
 */

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuthService oAuthService;
    private final MemberRepository memberRepository;

//    private final MemberRepository memberRepository;
//    private final RestTemplate restTemplate;
//    private final RedisTemplate redisTemplate;


    // == code 필요할 때 (시작)== //
    /**
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/",
                "/login/**",
                "/auth/not-secured",
                "/google/callback/**"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .cors().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(authorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2")
                .and()
                 .userInfoEndpoint()
                .userService(oAuth2UserService());
    }
    **/
    // == code 필요할 때 (끝)== //



    // == 실제 운영 (시작)== //
///**
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/",
                "/login/**",
                "/auth/not-secured",
                "/auth/deleteCookie"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .cors().disable()
                .authorizeRequests()
                .antMatchers("/comment/**").permitAll()
                .anyRequest().authenticated();


        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new ExceptionHandlerFilter(), tokenAuthenticationFilter().getClass());
    }
//**/

    // == 실제 운영 (끝) == //

    public TokenAuthenticationFilter tokenAuthenticationFilter(){
        TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter(oAuthService, memberRepository);
        //return new TokenAuthenticationFilter(new OAuthService(memberRepository, restTemplate, redisTemplate));
        return tokenAuthenticationFilter;
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new CustomOAuth2UserService();
    }

    @Bean
    @ConfigurationProperties("security.oauth2.client")
    public OAuth2ProtectedResourceDetails googleResourceDetails() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    public FilterRegistrationBean<OpenEntityManagerInViewFilter> openEntityManagerInViewFilter() {
        FilterRegistrationBean<OpenEntityManagerInViewFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new OpenEntityManagerInViewFilter());
        filterFilterRegistrationBean.setOrder(Integer.MIN_VALUE); // 예시를 위해 최우선 순위로 Filter 등록
        return filterFilterRegistrationBean;
    }
}

