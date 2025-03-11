package com.hwanu.backend.security.filter;

import com.hwanu.backend.security.ApiMemberDetailsService;
import com.hwanu.backend.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT를 이용한 인증 필터
 * Spring Security의 `OncePerRequestFilter`를 확장하여 모든 요청에 대해 JWT 검증을 수행함.
 */
@Log4j2
@RequiredArgsConstructor  // 생성자를 자동 생성 (final 필드 초기화)
public class TokenCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;  // JWT 관련 유틸리티 클래스
    private final ApiMemberDetailsService apiMemberDetailsService;  // 사용자 정보 로드 서비스

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

        try {
            //  요청에서 JWT 토큰을 추출하고, 유효성 검증 후 사용자 이메일 반환
            String email = validateAccessToken(request);

            //  인증되지 않은 상태이고, 유효한 이메일이 있는 경우 Spring Security에 인증 정보 저장
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = apiMemberDetailsService.loadUserByUsername(email); // 이메일 기반으로 사용자 정보 조회

                //  Spring Security의 인증 토큰 생성 (비밀번호는 필요하지 않음)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //  현재 요청에 대해 SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            //  필터 체인의 다음 필터로 이동 (정상적인 요청 처리 진행)
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("JWT Authentication Error: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"); // 401 응답 반환
        }
    }

    /**
     *  요청에서 JWT 토큰을 추출하고 검증한 후, 유효하면 이메일을 반환하는 메서드
     *
     * @param request 현재 HTTP 요청
     * @return 인증된 사용자의 이메일 (유효하지 않으면 null 반환)
     */
    private String validateAccessToken(HttpServletRequest request) {
        String headerStr = request.getHeader("Authorization"); // 요청 헤더에서 Authorization 값 가져오기

        //  Authorization 헤더가 없거나 Bearer 타입이 아니면 null 반환
        if (headerStr == null || !headerStr.startsWith("Bearer ")) {
            return null;
        }

        //  "Bearer " 부분을 제거하고 JWT 토큰만 추출
        String token = headerStr.substring(7);

        try {
            //  JWT 토큰을 검증하고, 유효하면 이메일 반환
            return jwtUtil.getEmailFromToken(token);
        } catch (MalformedJwtException e) { // 잘못된 형식의 JWT (ex: 구조가 올바르지 않음)
            log.error("Malformed JWT: {}", e.getMessage());
        } catch (SignatureException e) { // JWT 서명이 올바르지 않음
            log.error("Invalid JWT Signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) { // JWT가 만료됨
            log.error("Expired JWT: {}", e.getMessage());
        } catch (Exception e) { // 기타 JWT 관련 예외 처리
            log.error("Invalid JWT: {}", e.getMessage());
        }

        return null; // JWT가 유효하지 않으면 null 반환
    }
}