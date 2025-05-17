package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtTokenErrorType implements ErrorType {

    EXPIRED_ACCESS_TOKEN("EXPIRED_ACCESS_TOKEN", "Access Token의 만료기간이 지났습니다."),
    UNSUPPORTED_TOKEN("UNSUPPORTED_TOKEN", "지원하지 않는 TOKEN 유형입니다."),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않는 토큰입니다.");

    private final String code;
    private final String msg;
}
