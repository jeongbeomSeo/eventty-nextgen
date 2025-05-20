package com.eventty.eventtynextgen.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private final String JWT_SCHEMA_NAME = "JwtAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEMA_NAME))
            .components(components())
            .info(info());
    }

    private Components components() {
        return new Components()
            .addSecuritySchemes(JWT_SCHEMA_NAME, securityScheme());
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
            .name(JWT_SCHEMA_NAME)
            .type(Type.HTTP)
            .scheme("bearer")
            .in(In.HEADER)
            .bearerFormat("Authorization");
    }

    private Info info() {
        return new Info()
            .title("Eventty_nextgen API 명세서")
            .description("Eventty_nextgen API 명세서입니다.")
            .version("1.0");
    }
}
