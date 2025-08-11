package com.hwanu.backend.security.encoderdecoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * JwtDecoder 설정(HS256 대칭키).
 *
 * - application.properties 의 jwt.secret 값을 사용.
 * - 사실 spring.security.oauth2.resourceserver.jwt.secret-key 로도 자동 구성되지만
 *   Bean으로 명시해두면 코드만 봐도 키 전략이 보이고 테스트 주입도 쉬워집니다.
 */
@Configuration
public class JwtDecoderConfig {

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}