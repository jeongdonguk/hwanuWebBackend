package com.hwanu.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JwtUtil {

    private final Key key;  // JWT 서명을 위한 비밀 키
    private final long expiration;  // 토큰 만료 시간 (밀리초 단위)
    private final long refreshExpiration;


    public JwtUtil(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.expiration}") long expiration,
                   @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());  // HMAC SHA 키 생성
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    // JWT 토큰 생성
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();  // payload에 담을 데이터
        claims.put("role", role);  // 사용자의 역할(role) 추가

        return Jwts.builder()
                .setClaims(claims)  // payload에 role 추가
                .setSubject(email)   // subject를 사용자 email로 설정
                .setIssuedAt(new Date())  // 토큰 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))  // 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)  // 서명 알고리즘 적용 (HMAC SHA256)
                .compact();  // JWT 문자열로 변환하여 반환
    }

    // jwt 리프레시 토큰 만들기
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT Claims 파싱 (내부 메서드)
     * - 서명 키를 이용하여 토큰을 검증하고, payload(Claims) 정보를 가져옴
     * @param token 파싱할 JWT 토큰 문자열
     * @return 토큰의 Claims (payload 데이터)
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // 서명 키 설정
                .build()
                .parseClaimsJws(token)  // 토큰을 파싱하여 유효성 검사
                .getBody();  // Claims 객체 반환 (payload 정보)
    }

    // 토큰에서 이메일(subject) 추출
    public String getEmailFromToken(String token) {

        return parseClaims(token).getSubject();  // subject(email) 값 반환
    }

    //토큰에서 만료일 추출
    //@return 토큰에 저장된 만료일 정보
    public LocalDateTime getExpiresAtFromToken(String token) {
        return (LocalDateTime) parseClaims(token).get("expires_at");
    }




    /**
     * ✅ JWT 검증 메서드
     * - 토큰이 유효하면 true 반환
     * - 만료되었거나 변조된 토큰이면 false 반환
     * @param token 검증할 JWT 토큰
     * @return 유효성 결과 (true/false)
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                log.info("Token is null : 현재 로그인되어있지 않은 사용자입니다.");
                return false;
            }

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true; // 유효한 토큰
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("JWT 서명 검증 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.error("잘못된 JWT 토큰: {}", e.getMessage());
        }
        return false; // 유효하지 않은 토큰
    }


}