package com.hwanu.backend.security.issuer;

import com.hwanu.backend.domain.Member;
import com.hwanu.backend.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtIssuer {


    private final JwtEncoder jwtEncoder;  // JWT 서명을 위한 비밀 키
    private final long expiration;  // 토큰 만료 시간 (밀리초 단위)
    private final long refreshExpiration;


    public JwtIssuer(JwtEncoder jwtEncoder,
                     @Value("${jwt.expiration}") long expiration,
                     @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.jwtEncoder = jwtEncoder;  // HMAC SHA 키 생성
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }


    //  Access 토큰 발급: sub=email, role 등 필요한 최소 클레임만
    public String generateAccessToken(Member member) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(member.getEmail())
                .claim("nickname", member.getNickname())
                .claim("role", member.getRole())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return  jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    // Refresh 토큰 발급: 재발급 전용.
    public String generateRefreshToken(String email) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshExpiration))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

}
