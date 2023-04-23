package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("access_token");

        if (accessToken != null) {
            log.info("access_token 있음");
            URL url = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            log.info("responseCode={}", responseCode);

            PrincipalDetails principalDetails = new PrincipalDetails();
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.info("유효하지 않음");
                // Access Token이 유효하지 않은 경우 처리
                // 예: 401 Unauthorized 응답 반환
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        else {
            log.info("access_token 없음");
        }

        filterChain.doFilter(request, response);
    }
}
