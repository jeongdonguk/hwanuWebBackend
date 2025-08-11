package com.hwanu.backend.security.resolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;


/**
 * Access 토큰 추출 전략
 * 1) Authorization 헤더에서 "Bearer {token}" 우선
 * 2) 없으면 지정한 쿠키 이름에서 토큰 추출(웹 HttpOnly 쿠키 전략과 호환)
 *
 * ※ Refresh 토큰은 여기서 다루지 않습니다.
 *   Refresh는 /auth/refresh 엔드포인트에서만 따로 처리하는 것이 보안 모범 사례입니다.
 */
@RequiredArgsConstructor
public class CookieFirstBearerTokenResolver implements BearerTokenResolver {

    private final String cookieName;  // hwanuAccessToken

    @Override
    public String resolve(HttpServletRequest request) {

        String uri = request.getRequestURI();
        // ★ 토큰을 읽지 말아야 할 경로들
        if (uri.startsWith("/auth/") || uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
            return null;
        }

        // 1) Authorization 헤더
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 제거
        }
        // 2) 쿠키
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null; // 토큰 없음
    }

}
