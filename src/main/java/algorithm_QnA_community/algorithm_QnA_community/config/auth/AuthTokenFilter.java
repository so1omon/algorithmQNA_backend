package algorithm_QnA_community.algorithm_QnA_community.config.auth;


import algorithm_QnA_community.algorithm_QnA_community.config.auth.dto.AccessTokenAndRefreshUUID;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.TokenAuthenticationException;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config.auth
 * fileNmae         : AuthTokenFilter
 * author           : janguni
 * date             : 2023-06-05
 * description      : 토큰 인증 필터
 *                    흐름
 *                    -정상 흐름
 * *                          (1)상황
 * *                              - accessToken이 유효한 경우 또는
 * *                              - refreshToken이 유효한 경우
 * *                          (2)처리
 * *                              - authentication객체 생성 후 쿠키에 accessToken과refreshUUID값 넣어서 반환
 * *
 *  *                      -예외 처리
 * *                          (1)상황
 * *                              - accessToken이 유효하지 않은 상황에서 refreshToken도 유효하지 않은 경우
 * *                          (2)처리
 * *                              - TokenAuthenticationException 발생 후 AuthEntryPointJwt에서 예외처리 진행
 *
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/06/05       janguni         최초 생성
 */
@RequiredArgsConstructor
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Value("${cookie.domain}")
    private String domain;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        boolean authenticateFlag=false;
        log.info("doFilterInternal에 들어옴");
        // admin 계정
        String isAdmin = request.getHeader("isAdmin");
        if (isAdmin!=null && isAdmin.equals("true")){
            Optional<Member> findAdminMember = memberRepository.findById(1L);
            if (!findAdminMember.isPresent()) throw new TokenAuthenticationException("admin계정 없음");
            createAuthentication(findAdminMember.get());
            filterChain.doFilter(request, response);
        }

        // 액세스 토큰과 refreshUUID 값 추출
        AccessTokenAndRefreshUUID accessTokenAndRefreshUUID = extractAccessTokenAndRefreshUUID(request);
        if (accessTokenAndRefreshUUID==null) { // 쿠키가 없을 경우
            log.info("쿠키가 없음");
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = accessTokenAndRefreshUUID.getAccessToken();
        String refreshUUID = accessTokenAndRefreshUUID.getRefreshUUID();
        String refreshToken;

        log.info("accessToken={}", accessToken);
        log.info("refreshUUID={}", refreshUUID);

        // accessToken 인증
        if (accessToken!=null) {
            if (tokenProvider.validateAccessToken(accessToken)){
                log.info("access 인증 완료");
                String email = tokenProvider.getEmailWithAccessToken(accessToken);
                Optional<Member> findMember = memberRepository.findByEmail(email);
                if (findMember.isPresent()) {
                    log.info("사용자 존재!");
                    createAuthentication(findMember.get());
                    authenticateFlag=true;
                }
                else {
                    log.info("사용자 존재x");
                }
            }
            else {
                log.info("access 인증 실패");
            }
        }

        // refreshToken 인증
        if (refreshUUID!=null && authenticateFlag==false) {
            try {
                ValueOperations<String, String> vop = redisTemplate.opsForValue();
                refreshToken = vop.get(refreshUUID);
            } catch (Exception e) {
                log.info("refreshUUID redis에 없음");
                throw new TokenAuthenticationException("토큰예외");
            }

            if (tokenProvider.validateRefreshToken(refreshToken)) {
                log.info("refreshToken 인증 완료");
                String memberEmail = tokenProvider.getEmailWithRefreshToken(refreshToken);
                Optional<Member> findMember = memberRepository.findByEmail(memberEmail);
                if (findMember.isPresent()) {
                    createAuthentication(findMember.get());
                    accessToken = tokenProvider.createAccessToken(findMember.get().getEmail(), findMember.get().getRole().value());
                    authenticateFlag = true;
                }
            } else {
                log.info("refresh 인증 실패");
            }
        }

        // 인증 통과 시 Cookie에 accessToken, refreshUUID 값 담음
        if (authenticateFlag) {

            HttpServletResponse httpResponse = (HttpServletResponse) response;

            Cookie accessCookie = new Cookie("access_token", accessToken);
            accessCookie.setPath("/");
            accessCookie.setDomain(domain);
            //accessCookie.setSecure(true);
            accessCookie.setHttpOnly(true);
            Cookie refreshCookie = new Cookie("refresh_uuid", refreshUUID);
            //refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setDomain(domain);
            refreshCookie.setHttpOnly(true);

            httpResponse.addCookie(accessCookie);
            httpResponse.addCookie(refreshCookie);
        }

        filterChain.doFilter(request, response);
    }

    // 쿠키에 있는 accessToken, refreshToken 추출
    private AccessTokenAndRefreshUUID extractAccessTokenAndRefreshUUID(HttpServletRequest request) {
        String accessToken = null;
        String refreshUUID = null;
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            return null;
        }

        for (Cookie c: cookies) {
            String name = c.getName();
            if (name.equals("access_token")){
                accessToken = c.getValue();
            } else if (name.equals("refresh_uuid")) {
                refreshUUID = c.getValue();
            }
        }

        return new AccessTokenAndRefreshUUID(accessToken, refreshUUID);
    }


    private void createAuthentication(Member member) {
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
