package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.config.auth
 * fileName       : AccessDeniedHandler
 * author         : solmin
 * date           : 2023/05/22
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/22        solmin       최초 생성
 *                                SecurityConfig configure -> AccessDeniedHandler 주입 시 필요
 *                                특정 Role을 지니지 않는 유저의 요청 시 Unauthorized response body를 리턴
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpStatus unauthorized = org.springframework.http.HttpStatus.UNAUTHORIZED;
        Res errResponse = Res.res(new DefStatus(unauthorized.value(), "해당 API를 사용할 수 있는 권한이 없습니다."));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(unauthorized.value());
        String json = new ObjectMapper().writeValueAsString(errResponse);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}
