package com.eventty.eventtynextgen.base.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiName {

    USER("/api/v1/user/**"),
    EVENTS("/api/v1/events/**"),

    // AUTH
    AUTH_CODE("/api/v1/auth/code/**"),
    AUTH_LOGIN("/api/v1/auth/login/**"),
    AUTH("/api/v1/auth/**"),

    // ASSET
    ASSET_FILE("/api/v1/asset/file/**")
    ;

    private final String pattern;
}
