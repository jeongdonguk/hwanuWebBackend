package com.hwanu.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JWTUtil {

    private final Key key;  // JWT 서명을 위한 비밀 키
    private final long expirationTime;  // 토큰 만료 시간 (밀리초 단위)


    public JWTUtil(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());  // HMAC SHA 키 생성
        this.expirationTime = expiration;
    }

    // JWT 토큰 생성
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();  // payload에 담을 데이터
        claims.put("role", role);  // 사용자의 역할(role) 추가

        return Jwts.builder()
                .setClaims(claims)  // payload에 role 추가
                .setSubject(email)   // subject를 사용자 email로 설정
                .setIssuedAt(new Date())  // 토큰 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)  // 서명 알고리즘 적용 (HMAC SHA256)
                .compact();  // JWT 문자열로 변환하여 반환
    }

    // 토큰에서 이메일(subject) 추출
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();  // subject(email) 값 반환
    }

    //토큰에서 역할(role) 추출
    //@return 토큰에 저장된 role 정보
    public String getRoleFromToken(String token) {
        return (String) parseClaims(token).get("role");  // payload에서 role 값 반환
    }

    /**
     * WT 토큰 검증
     * @param token 검증할 JWT 토큰 문자열
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);  // 토큰을 파싱하여 유효성 확인
            return true;  // 검증 성공 시 true 반환
        } catch (JwtException e) {  // 토큰이 유효하지 않다면 예외 발생
            log.error("Invalid JWT token: {}", e.getMessage());  // 로그 출력
            return false;  // 검증 실패 시 false 반환
        }
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
}