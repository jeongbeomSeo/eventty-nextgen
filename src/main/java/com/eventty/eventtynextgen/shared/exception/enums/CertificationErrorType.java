package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CertificationErrorType implements ErrorType {

    NOT_ALLOWED_APP_NAME("NOT_ALLOWED_APP_NAME", "허용되지 않은 APP NAME입니다."),
    MISMATCH_SECRET_KEY("MISMATCH_SECRET_KEY", "API 토큰 인증키가 일치하지 않습니다.");

    private final String code;
    private final String msg;
}
