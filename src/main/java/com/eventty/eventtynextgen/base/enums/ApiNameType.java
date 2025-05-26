package com.eventty.eventtynextgen.base.enums;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

@Getter
@RequiredArgsConstructor
public enum ApiNameType {

    // swagger
    SWAGGER_UI("any","/swagger-ui/**"),
    SWAGGER_API("any", "/v3/api-docs/**"),

    // certification
    LOGIN(POST.name(), "/api/v1/certification/login"),
    CODE_EXISTS(GET.name(), "/api/v1/certification/code/exists"),
    CODE_SEND(POST.name(), "/api/v1/certification/code"),
    CODE_VALIDATE(POST.name(), "/api/v1/certification/code/validate"),

    // user
    SIGNUP(POST.name(), "/api/v1/user"),
    UPDATE(PATCH.name(), "/api/v1/user"),
    USER_DELETE(DELETE.name(), "/api/v1/user"),
    ACTIVATE_DELETED_USER(PATCH.name(), "/api/v1/user/*/status"),
    FIND_EMAIL(GET.name(), "/api/v1/user/email"),
    CHANGE_PASSWORD(PATCH.name(), "/api/v1/user/password");

    private final String method;
    private final String pattern;
}
