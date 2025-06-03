package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtTokenErrorType implements ErrorType {

    EXPIRED_TOKEN("EXPIRED_TOKEN", "Token의 만료기간이 지났습니다."),
    UNSUPPORTED_TOKEN("UNSUPPORTED_TOKEN", "지원하지 않는 TOKEN 유형입니다."),
    ILLEGAL_STATE_TOKEN("ILLEGAL_STATE_TOKEN", "유효하지 않는 토큰입니다."),
    FAILED_SIGNATURE_VALIDATION("FAILED_SIGNATURE_VALIDATION", "토큰 서명 검증에 실패했습니다."),
    UNKNOWN_VERIFY_ERROR("UNKNOWN_VERIFY_ERROR", "토큰 검증 과정에서 알 수 없는 예외가 발생했습니다.");

    private final String code;
    private final String msg;
}
