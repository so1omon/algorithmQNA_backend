package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {

            //토큰의 유효기간 만료
            log.error("만료된 토큰입니다");
            //response.sendError(403, String.valueOf(new Status(403,"사용자 인증에 실패했습니다. 로그인 후 다시 시도해주세요.")));
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.getWriter().write(new ObjectMapper().writeValueAsString(new Status(403, "사용자 인증에 실패했습니다. 로그인 후 다시 시도해주세요.")));
            response.sendRedirect("/auth/not-secured");
        }
    }

    @Data
    public class Status {
        private int code;
        private String message;

        public Status(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }


}
