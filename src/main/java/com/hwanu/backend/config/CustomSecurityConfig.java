package com.hwanu.backend.config;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

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
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 X (JWT 인증)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // 인증 없이 접근 가능
                        .requestMatchers("/posts/**").authenticated() // 회원만 게시판 API 접근 가능
                        .anyRequest().authenticated() // 기타 API는 로그인 필요
                );
        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
