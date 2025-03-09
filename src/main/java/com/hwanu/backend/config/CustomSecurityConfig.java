package com.hwanu.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 설정 클래스임을 명시 (스프링 컨테이너가 자동으로 빈 등록)
@Log4j2       // Log4j2 라이브러리 사용해서 로그 출력 (log.info() 사용 가능)
@EnableWebSecurity // Spring Security 활성화 (웹 보안 기능을 설정할 수 있도록 함)
@EnableGlobalMethodSecurity(prePostEnabled = true)
// @PreAuthorize, @PostAuthorize 어노테이션으로 메서드별 권한 체크 가능하게 함 -- admin 계정등에 대한 설정가능
@RequiredArgsConstructor
// final 필드에 대해 자동으로 생성자 주입 (의존성 주입을 간편하게 사용)
public class CustomSecurityConfig {

    // 🔑 비밀번호 암호화 설정 (BCrypt 방식 사용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호를 암호화해서 저장
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("------------web configure---------------"); // 로그 출력

        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        // CSS, JS, 이미지 같은 정적 리소스에 대해서는 보안 필터 적용 안 함
    }

    // 🔑 Spring Security 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        log.info("------------security configure---------------"); // 로그 출력

        // CSRF(Cross-Site Request Forgery) 방어 기능 비활성화
        http.csrf().disable(); // 세션상태 (메모리에 사용자 정보가 있는 상태에서 악성 사이트 진입시 내 정보로 회원 탈퇴 등 공격) => 세션을 사용하지 않으면 필요없

        // JWT 같은 방식에서 세션을 사용하지 않기 위한 설정 -- 세션을 미사용. 사용할 경우 토큰을 사용하는 의미가 사라짐
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 필터 체인 생성
        return http.build();
    }
}
