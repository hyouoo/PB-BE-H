package com.example.purebasketbe.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private final String SCHEME_NAME = "BearerAuth";
    // Bearer 빼고 붙여 넣으면 됨
    @Bean
    public OpenAPI customOAS() {
        return new OpenAPI().components(
                        new Components().addSecuritySchemes(
                                SCHEME_NAME, new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Pure Basket E-commerce Project")
                .description("HH-Final-6")
                .version("0.1.0");
    }
}
