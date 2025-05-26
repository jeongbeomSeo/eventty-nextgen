package com.eventty.eventtynextgen.base.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiName {

    // swagger
    SWAGGER("/swagger-ui,/v3/api-docs"),
    HEALTH("/health"),

    // eventty
    CERTIFICATION("/api/v1/certification"),
    USER("api/v1/user");

    private final String pattern;
}
