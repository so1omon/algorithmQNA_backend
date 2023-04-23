package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
@Configuration
@RequiredArgsConstructor
@EnableOAuth2Client
public class SecurityConfig {

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        //public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/","/oauth2callback").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin().disable()
                .oauth2Login()
                .loginPage("/login")
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
//                .and()
//                .defaultSuccessUrl("/success")
//                .failureUrl("/fail");

                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2callback");

        //"/oauth2/callback" 경로로 들어오는 요청에 대해 필터 추가
        //http.addFilterBefore(new OAuth2TokenAuthenticationFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails resource) {
        return new OAuth2RestTemplate(resource, oauth2ClientContext);
    }

    @Bean
    @ConfigurationProperties("security.oauth2.client")
    public OAuth2ProtectedResourceDetails googleResourceDetails() {
        return new AuthorizationCodeResourceDetails();
    }
}
**/


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberRepository memberRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                    .authorizeRequests()
                    .antMatchers("/", "/oauth2/token/new","/oauth2").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                    //.loginPage("/login")
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(authorizationRequestRepository())
                .and()
                    .redirectionEndpoint()
                    .baseUri("/oauth2")
                .and()
                    .userInfoEndpoint()
                    .userService(oAuth2UserService())
                .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler())
                    .failureHandler(oAuth2AuthenticationFailureHandler());

        //http.addFilterBefore(new CustomOAuth2LoginAuthenticationFilter(authorizationRequestRepository()), UsernamePasswordAuthenticationFilter.class);
       http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }



    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(){
        return new TokenAuthenticationFilter();
    }

    @Bean
    public RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails resource) {
            var factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(3000);
            factory.setReadTimeout(3000);
            return new RestTemplate(factory);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new CustomOAuth2UserService(memberRepository);
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new CustomOAuth2AuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new CustomOAuth2AuthenticationFailureHandler();
    }

    @Bean
    @ConfigurationProperties("security.oauth2.client")
    public OAuth2ProtectedResourceDetails googleResourceDetails() {
        return new AuthorizationCodeResourceDetails();
    }






}

