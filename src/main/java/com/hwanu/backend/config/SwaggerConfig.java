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

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))  // JWT 인증 추가
                .components(new Components().addSecuritySchemes("BearerAuth",
                        new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));  //  Bearer 토큰 설정
    }

    private Info apiInfo() {
        return new Info()
                .title("동욱이 API")
                .description("멍멍")
                .version("1.0.0");
    }

}