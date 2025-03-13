package com.hwanu.backend.security.filter;

import com.hwanu.backend.security.CustomAuthentication;
import com.hwanu.backend.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT를 이용한 인증 필터
 * Spring Security의 `OncePerRequestFilter`를 확장하여 모든 요청에 대해 JWT 검증을 수행함.
 */
@Log4j2
@RequiredArgsConstructor  // 생성자를 자동 생성 (final 필드 초기화)
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;  // JWT 관련 유틸리티 클래스

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI(); // 현재 요청된 URL 경로 가져오기

        // JWT 검증을 수행하지 않는 경로 목록 (Swagger, 공용 API, 헬스 체크 등)
        List<String> excludedPaths = List.of("/test");
        // 특정 경로에 대해 JWT 필터 적용 제외
        if (excludedPaths.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response); // 필터를 건너뛰고 다음 필터로 진행
            return;
        }

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(new CustomAuthentication(email));
            }
        }
        filterChain.doFilter(request, response);

    }

}