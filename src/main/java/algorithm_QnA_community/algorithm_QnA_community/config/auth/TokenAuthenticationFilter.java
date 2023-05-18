package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.config.exception.TokenAuthenticationException;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.MemberInfoRes;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config.auth
 * fileNmae         : TokenAuthenticationFilter
 * author           : janguni
 * date             : 2023-05-02
 * description      : accessToken과 refreshUUID의 유효성 검증
 *                      - 정상 흐름
 *                          (1) 상황
 *                              - accessToken이 유효한 경우
 *                              - refreshUUID로 accessToken을 재발급 한 경우
 *                          (2) 처리
 *                              - authentication 객체 생성 후 쿠키에 accessToken과 refreshUUID값 넣어서 반환
 *
 *                      - 예외 처리
 *                          (1) 상황
 *                              - accessToken, refreshUUID 둘 중 하나라도 값이 없을 경우
 *                              - accessToken이 유효하지 않을 상황에서 refreshUUID로 accessToken 재발급을 못한 경우
 *                          (2) 처리
 *                              - runtimeException 발생 후 ExceptionHandlerFilter에서 예외처리 진행
 *
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/05/02       janguni         최초 생성
 * 2023/05/10        solmin         [리뷰 부탁!!!] 토큰 없을 때 그냥 패싱
 */


@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter implements InitializingBean {

    private final OAuthService oAuthService;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, AuthenticationException {
        try {
            // 액세스 토큰과 refreshUUID 값 추출
            String accessToken = request.getHeader("access_token");
            String refreshUUID = request.getHeader("refreshUUID");


            // ============ accessToken & refreshUUID 로 토큰 유효 검증 로직 ============ //
            if (accessToken != null & refreshUUID != null) { // 두 개의 값이 모두 있을 경우

                // accessToken 먼저 유효 검증
//                URL url = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//
//                // 유효하다면 200, 그렇지 않다면 400
//                int responseCode = connection.getResponseCode();
//
//                // accessToken 유효한 경우
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    log.info("accessToken이 유효함");
//                    createAuthentication(); //authentication 객체 생성
//                }
                MemberInfoRes memberInfo = oAuthService.getMemberInfo(accessToken, "state");
                if (memberInfo!=null) {
                    Member findMember = memberRepository.findByEmail(memberInfo.getEmail()).get(); // 예외처리 해야함!!!
                    log.info("findMember.email={}", findMember.getEmail());
                    createAuthentication(findMember);
                    log.info("authentication 객체 생성 완료!!");
                }

                // accessToken이 유효하지 않은 경우
                else {
                    accessToken = oAuthService.sendTokens(refreshUUID); // refreshUUID로 accessToken 재발급

                    // accessToken 재발급에 성공한 경우
                    if (accessToken != null) {
                        MemberInfoRes memberInfo2 = oAuthService.getMemberInfo(accessToken, "state");

                        Member findMember2 = memberRepository.findByEmail(memberInfo2.getEmail()).get(); // 예외처리 해야함!!!

                        createAuthentication(findMember2);
                    }

                    // refreshUUID 값이 잘못되었거나, refreshToken이 만료됐을 경우
                    else {
                        throw new TokenAuthenticationException("토큰예외"); // Exception!
                    }
                }
            }

            // accessToken과 refreshUUID 둘 중 하나라도 없을 경우
            else {
                log.info("토큰 빠트림");
                throw new TokenAuthenticationException("토큰예외"); // Exception!
            }

            // Cookie에 accessToken, refreshUUID 값 담음
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setSecure(true);
            accessCookie.setHttpOnly(true);
            Cookie refreshCookie = new Cookie("refreshUUID", refreshUUID);
            refreshCookie.setSecure(true);
            refreshCookie.setHttpOnly(true);

            httpResponse.addCookie(accessCookie);
            httpResponse.addCookie(refreshCookie);

            log.info("필터를 통과~~~");
            filterChain.doFilter(request, response);
        } catch (TokenAuthenticationException e) {
            throw e;
        }
    }

    // authentication 객체 생성
    private void createAuthentication(Member member) {
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
