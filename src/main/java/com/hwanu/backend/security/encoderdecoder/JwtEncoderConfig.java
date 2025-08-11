package com.hwanu.backend.security.encoderdecoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtEncoderConfig {
    @Bean
    public JwtEncoder jwtEncoder(@Value("${jwt.secret}") String secret) {
        // HS256용 대칭키
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        // 인코더는 JWKSource 기반 생성 → 대칭키는 ImmutableSecret 사용
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }


}
