package com.hwanu.backend.security.convert;

import com.hwanu.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JWT(서명/만료 검증 완료) → Authentication
 *
 * - Jwt.getSubject() 를 이메일(주식별자)로 사용한다고 가정.
 * - DB에서 회원을 조회하여 존재/상태/권한을 확정.
 * - 권한은 "ROLE_" prefix를 붙여 Spring Security 규칙을 따름.
 *
 * 실패 시 JwtException을 던지면 401(UNAUTHORIZED) 처리가 됩니다.
 */
@Component
@RequiredArgsConstructor
public class DbBackendJwtAuthConverter implements Converter<Jwt, JwtAuthenticationToken> {

    private final MemberRepository memberRepository;

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        String email = jwt.getSubject(); // 발급 시 setSubject(email)

        var member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new org.springframework.security.oauth2.jwt.JwtException("User not found"));

        // 필요하다면 탈퇴/정지 등 상태 체크
        // if (!member.isActive()) throw new JwtException("User disabled");

        // DB 기준 권한(토큰의 role 클레임보다 우선)
        String role = member.getRole() == null ? "USER" : member.getRole();
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        // principal(name)은 email로 지정
        return new JwtAuthenticationToken(jwt, authorities, email);
    }
}


