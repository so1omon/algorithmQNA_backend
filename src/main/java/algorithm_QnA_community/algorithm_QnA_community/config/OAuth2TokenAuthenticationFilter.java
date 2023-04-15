//package algorithm_QnA_community.algorithm_QnA_community.config;
//
//import algorithm_QnA_community.algorithm_QnA_community.service.auth.OAuthService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.client.OAuth2RestTemplate;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Slf4j
//public class OAuth2TokenAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private OAuthService oAuthService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if ("/oauth2/callback".equals(request.getRequestURI())) {
//            log.info("/login post방식으로 요청이 들어옴");
//            String code = request.getParameter("code");
//            OAuth2AccessToken token = oAuthService.login(code);
//
//            response.set
//        }
//        filterChain.doFilter(request, response);
//    }
//
//}
//
