package com.hwanu.backend.config;

import com.hwanu.backend.security.convert.DbBackendJwtAuthConverter;
import com.hwanu.backend.security.resolver.CookieFirstBearerTokenResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Resource Server 기반 보안 설정의 중심.
 * - 커스텀 필터 없이 표준 필터 체인을 사용
 * - 토큰 추출(BearerTokenResolver)과 토큰→인증 변환(JwtAuthenticationConverter)만 커스터마이즈
 */
@Configuration // 설정 클래스임을 명시 (스프링 컨테이너가 자동으로 빈 등록)
@Log4j2       // Log4j2 라이브러리 사용해서 로그 출력 (log.info() 사용 가능)
@EnableWebSecurity // Spring Security 활성화 (웹 보안 기능을 설정할 수 있도록 함)
@EnableMethodSecurity
@RequiredArgsConstructor
// final 필드에 대해 자동으로 생성자 주입 (의존성 주입customSecurityConfig 간편하게 사용)
public class CustomSecurityConfig {

    private final DbBackendJwtAuthConverter dbConverter;

    // 인증 없이 접근을 허용할 엔트포인트(화이트 리스트)
    private static final String[] PERMIT_URLS = {
            "/auth/**", // 인증, 회원가입 등을 진행하는 api
            "/swagger-ui/**", "/v3/api-docs/**", // Swagger 문서
            "/board/public", // 공개 게시글 로그인 없이 사이트 접근한 사람용
            "/board/list", // 글접근을 회원과 무관하게 가능하도록 수정
            "/board/postRead", // 글읽기 페이지
            "/actuator/health" // 헬스체크
    };


    /** 헤더→쿠키 순으로 Access 토큰을 추출하는 리졸버 빈 등록 */
    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return new CookieFirstBearerTokenResolver("hwanuAccessToken");
    }


    @Bean  // 시큐리티 필터체인 : 요청 -> 필터들 -> 인증처리
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   BearerTokenResolver bearerTokenResolver) throws Exception {

        log.info("SecurityFilterChain");
        http
                // CORS 설정 불필요. 세션 인증이 아닌 Jwt인증에서 필요없음
                .csrf(AbstractHttpConfigurer::disable)
                // Form Login: html 폼 기반 로그인 페이지 제공
                // rest api 서버에서는 불필요한 ui 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // HTTP Basic: 브라우저 팝업 기반 ID/PW 인증 방식
                //  - JWT 사용 시 불필요하므로 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // CORS: 로컬 개발 편의 + 운영 전환 수월하도록 패턴 기반 허용
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                // Logout: 세션 기반 로그아웃 기능
                //  - JWT는 서버에서 세션을 유지하지 않으므로 기본 로그아웃 기능이 불필요
                .logout(AbstractHttpConfigurer::disable)
                // ==== 세션 정책 ====
                // SessionCreationPolicy.STATELESS:
                //  - 서버가 세션(HttpSession)을 생성하지 않고, 요청 간 인증 정보를 유지하지 않음
                //  - 매 요청마다 JWT를 검증해야 함
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ==== 인가(Authorization) 규칙 ====
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스(css, js, 이미지 등)은 모두 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // PERMIT_URLS 배열에 정의된 URL 패턴은 모두 허용(인증 불필요)
                        .requestMatchers(PERMIT_URLS).permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated())
                // 401/403 응답을 JSON으로 통일 (학습/디버그에 도움)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req,res,ex)->{
                            res.setStatus(401);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\":\"unauthorized\"}");
                        })
                        .accessDeniedHandler((req,res,ex)->{
                            res.setStatus(403);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\":\"forbidden\"}");
                        })
                )
                // ==== OAuth2 Resource Server 설정 (JWT 인증) ====
                // Spring Security의 리소스 서버 모드를 활성화하여 JWT 인증 필터를 사용
                // ★ 표준 리소스 서버: 내부적으로 BearerTokenAuthenticationFilter 사용
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(bearerTokenResolver)   // 헤더→쿠키
                        .jwt(jwt -> jwt
                                // JwtDecoder는 별도 설정( security/decoder/JwtDecoderConfig )
                                .jwtAuthenticationConverter(dbConverter) // DB에서 유저/권한 로딩
                        )
                );

        return http.build();
    }


    /** CORS 설정 빈: 허용 출처/메서드/헤더/쿠키 전송 여부 지정 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",  // 로컬 개발 출처
                "https://hwanu.site"
        ));
        conf.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS")); // 허용 메서드
        conf.setAllowedHeaders(List.of("Authorization","Content-Type")); // 허용 헤더
//        conf.setExposedHeaders(List.of("Authorization")); // 브라우저에 노출할 응답 헤더
        conf.setAllowCredentials(true); // 쿠키/인증정보 전송 허용
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", conf); // 모든 경로에 위 정책 적용
        return src;
    }

}

