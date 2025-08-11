package com.hwanu.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // Swagger UI에 표시될 API 기본 정보 설정
    private Info apiInfo() {
        return new Info()
                .title("화누 API")
                .description("멍멍")
                .version("1.0.0");
    }

    /**
     * OpenAPI(Swagger) 설정 Bean 등록
     * - Swagger UI에서 JWT 인증을 테스트할 수 있도록 Security 설정 추가
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                // 보안요구 사항 추가
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))  // JWT 인증 추가
                .components(new Components().addSecuritySchemes("BearerAuth",
                        new SecurityScheme()
                                .name("Authorization")      // HTTP 헤더 이름
                                .type(SecurityScheme.Type.HTTP) // HTTP 기반 인증
                                .scheme("bearer")           // Bearer 방식 (JWT)
                                .bearerFormat("JWT")));     // 토큰 형식 명시
    }


}