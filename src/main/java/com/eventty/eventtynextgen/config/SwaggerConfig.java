package com.eventty.eventtynextgen.config;

import static com.eventty.eventtynextgen.base.constant.BaseConst.AUTHORIZATION_HEADER;
import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;

import com.eventty.eventtynextgen.base.constant.BaseConst;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
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
@OpenAPIDefinition(servers = {
    @Server(url = "http://localhost:8080", description = "Local server"),
    @Server(url = "https://eventty-nextgen.site", description = "Develop server")
})
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(AUTHORIZATION_HEADER).addList(CERTIFICATION_TOKEN_COOKIE_NAME))
            .components(components())
            .info(info());
    }

    private Components components() {
        return new Components()
            .addSecuritySchemes(AUTHORIZATION_HEADER, accessTokenSchema())
            .addSecuritySchemes(CERTIFICATION_TOKEN_COOKIE_NAME, certificationTokenSchema());
    }
    private SecurityScheme certificationTokenSchema() {
        return new SecurityScheme()
            .name(CERTIFICATION_TOKEN_COOKIE_NAME)
            .type(Type.APIKEY)
            .in(In.HEADER)
            .description("발급 받은 Certification Token(API 호출 권한)");
    }

    private SecurityScheme accessTokenSchema() {
        return new SecurityScheme()
            .name(AUTHORIZATION_HEADER)
            .type(Type.HTTP)
            .scheme("bearer")
            .in(In.HEADER)
            .bearerFormat("JWT")
            .description("발급 받은 Access Token(로그인한 사용자 세션 토콘)");
    }

    private Info info() {
        return new Info()
            .title("Eventty_nextgen API 명세서")
            .description("Eventty_nextgen API 명세서입니다.")
            .version("1.0");
    }
}
