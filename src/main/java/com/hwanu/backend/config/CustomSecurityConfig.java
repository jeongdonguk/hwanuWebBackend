package com.hwanu.backend.config;

import com.hwanu.backend.repository.MemberRepository;
import com.hwanu.backend.security.JwtUtil;
import com.hwanu.backend.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // 설정 클래스임을 명시 (스프링 컨테이너가 자동으로 빈 등록)
@Log4j2       // Log4j2 라이브러리 사용해서 로그 출력 (log.info() 사용 가능)
@EnableWebSecurity // Spring Security 활성화 (웹 보안 기능을 설정할 수 있도록 함)
@RequiredArgsConstructor
// final 필드에 대해 자동으로 생성자 주입 (의존성 주입을 간편하게 사용)
public class CustomSecurityConfig {

    /**
     * Spring Security 필터 설정
     * - JWT 인증 적용
     * - 특정 API는 인증 없이 접근 허용
     */
    private final JwtFilter jwtFilter;
    private final MemberRepository memberRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> (UserDetails) memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil, UserDetailsService userDetailsService) throws Exception {
//        log.info("securityFilterChain .... ");
        http
                .cors().configurationSource(corsConfigurationSource()) // CORS 설정 추가
                .and()
                .csrf().disable() // CSRF 비활성화 (REST API는 필요 없음)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login","/auth/**").permitAll() //  로그인 & 회원가입은 인증 없이 허용
                        .anyRequest().authenticated()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //  JwtFilter 생성 시 UserDetailsService도 함께 전달해야 함
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
//        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); // 프론트엔드 주소 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // 쿠키 기반 인증 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}

