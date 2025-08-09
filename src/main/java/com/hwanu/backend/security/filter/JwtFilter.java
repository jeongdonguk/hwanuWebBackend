package com.hwanu.backend.security.filter;

import com.hwanu.backend.security.CustomAuthentication;
import com.hwanu.backend.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT를 이용한 인증 필터
 * Spring Security의 `OncePerRequestFilter`를 확장하여 모든 요청에 대해 JWT 검증을 수행함.
 */
@Log4j2
@Component
@RequiredArgsConstructor  // 생성자를 자동 생성 (final 필드 초기화)
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;  // JWT 관련 유틸리티 클래스
//    private final UserDetailsService userDetailsService; //  UserDetailsService 추가

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI(); // 현재 요청된 URL 경로 가져오기
//        log.info("doFilterInternal .... ");

        // JWT 검증을 수행하지 않는 경로 목록 (Swagger, 공용 API, 헬스 체크 등)
        List<String> excludedPaths = List.of("/test","/auth/login");
        // 특정 경로에 대해 JWT 필터 적용 제외
        if (excludedPaths.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response); // 필터를 건너뛰고 다음 필터로 진행
            return;
        }

        //  1. JWT를 가져오는 로직 (쿠키에서 가져오기)
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("hwanuAccessToken".equals(cookie.getName())) { //  JWT가 저장된 쿠키 찾기
                    token = cookie.getValue();
                    log.info("쿠키에서 JWT 찾음: " + token);
                    break;
                }
            }
        }

        // 2. JWT가 없는 경우 로그 출력 후 필터 종료
        if (token == null) {
            log.warn(" JWT 토큰 없음, 인증 없이 진행");
            filterChain.doFilter(request, response);
            return;
        }

        // 3. JWT 검증
        JwtUtil.TokenRS tokenRS = jwtUtil.validateToken(token);
        if (tokenRS == JwtUtil.TokenRS.VALID) {
            String email = jwtUtil.getEmailFromToken(token);
//            log.info(" JWT 검증 성공, 사용자 이메일: " + email);

            // 4. SecurityContextHolder에 UserDetails 저장
            UserDetails userDetails = User.withUsername(email)
                    .password("") // 비밀번호 필요 없음
                    .authorities(new SimpleGrantedAuthority("USER")) // 기본 역할 설정
                    .build();

            //  4. SecurityContextHolder에 사용자 정보 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("SecurityContextHolder에 인증 정보 저장 완료");
        } else {
            log.warn("JWT 검증 실패");
            log.warn("JWT 검증 실패: " + tokenRS);
            response.getWriter().write("{\"error\": \"" + tokenRS + "\"}");
            return;
        }

        //  5. 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}