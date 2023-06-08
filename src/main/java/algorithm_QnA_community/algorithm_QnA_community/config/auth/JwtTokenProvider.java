package algorithm_QnA_community.algorithm_QnA_community.config.auth;

import algorithm_QnA_community.algorithm_QnA_community.config.exception.TokenAuthenticationException;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.config.auth
 * fileNmae         : JwtTokenProvider
 * author           : janguni
 * date             : 2023-06-05
 * description      : jwt토큰 생성 및 검증
 *
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023-06-05       janguni         최초 생성
 */

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.accessSecret}")
    private String accessSecret;

    @Value("${jwt.refreshSecret}")
    private String refreshSecret;

    private long accessTokenValidTime = 30 * 60 * 1000L;

    private long refreshTokenValidTime = 30 * 24 * 60 * 60 * 1000L;


    // accessToken 생성
    public String createAccessToken(String email, String roles) {
        Claims claims = Jwts.claims().setSubject(email); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, accessSecret)
                .compact();
    }

    // refreshToken 생성
    public String createRefreshToken(String email, String roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        Date now = new Date();

        // refresh Token 생성
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, refreshSecret)
                .compact();
        return refreshToken;
    }

    // accessToken 에서 회원 정보 추출
    public String getEmailWithAccessToken(String token) {
        return Jwts.parser().setSigningKey(accessSecret).parseClaimsJws(token).getBody().getSubject();
    }

    // refreshToken 에서 회원 정보 추출
    public String getEmailWithRefreshToken(String token) {
        return Jwts.parser().setSigningKey(refreshSecret).parseClaimsJws(token).getBody().getSubject();
    }

    // accessToken 유효성, 만료일자 확인
    public boolean validateAccessToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(accessSecret).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // refreshToken 유효성, 만료일자 확인
    public boolean validateRefreshToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshSecret).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
