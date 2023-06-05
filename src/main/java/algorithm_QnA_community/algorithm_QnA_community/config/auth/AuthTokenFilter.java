package algorithm_QnA_community.algorithm_QnA_community.config.auth;


import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RequiredArgsConstructor
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    private final MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilterInternal를 들어옴");
        String isAdmin = request.getHeader("isAdmin");

        if(isAdmin!=null && isAdmin.equals("true")){
            Member findMember = memberRepository.findById(1L).get(); // 예외처리 해야함!!!
            //createAuthentication(findMember); -> 관리자 Authentication 객체 생성 해야함
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = parseJwt(request);
        try{
            if(jwt != null && tokenProvider.validateToken(jwt)) {
                log.info("토큰 유효함!={}", jwt);
                String email = tokenProvider.getEmail(jwt);
                Optional<Member> findMember = memberRepository.findByEmail(email);
                createAuthentication(findMember.get());
//                UserDetails userDetails = userDetailService.loadUserByEmail(email);
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
//                        null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {

        // 액세스 토큰과 refreshUUID 값 추출
        String accessToken = null;
        //String refreshUUID = null;
        Cookie[] cookies = request.getCookies();
        if (cookies==null) return null;

        for (Cookie c: cookies) {
            String name = c.getName();
            if (name.equals("access_token")){
                accessToken = c.getValue();
//            } else if (name.equals("refresh_uuid")) {
//                refreshUUID = c.getValue();
            }
        }
        return accessToken;
//        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
//            return accessToken.substring(7, accessToken.length());
//        }
//        else {
//            return null;
//        }
    }

    private void createAuthentication(Member member) {
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
