package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorType implements ErrorType {

    // Auth Code
    AUTH_CODE_SAVE_ERROR("AUTH_CODE_SAVE_ERROR", "인증 코드 저장 중 오류가 발생했습니다."),
    EXPIRE_EMAIL_AUTH_CODE("EXPIRE_EMAIL_AUTH_CODE", "이메일 검증 코드가 만료되었습니다."),
    MISMATCH_EMAIL_AUTH_CODE("MISMATCH_EMAIL_AUTH_CODE", "이메일 검증 코드가 일치하지 않습니다."),

    // Authentication
    AUTH_PASSWORD_MISMATCH("AUTH_PASSWORD_MISMATCH", "패스워드가 일치하지 않습니다."),
    AUTH_USER_NOT_ACTIVE("AUTH_USER_NOT_ACTIVE", "해당 유저는 활성화 상태가 아닙니다."),
    JWT_TOKEN_EXPIRED("JWT_TOKEN_EXPIRED", "토큰 인증 기간이 지났습니다. 재발급을 시도하세요"),
    FAILED_TOKEN_VERIFIED("FAILED_TOKEN_VERIFIED", "토큰 검증에 실패했습니다."),

    // Authorization
    NOT_ALLOWED_AUTHORIZE_WITHOUT_AUTHENTICATION("NOT_ALLOWED_AUTHORIZE_WITHOUT_AUTHENTICATION", "인증되지 않은 사용자는 권한을 부여할 수 없습니다."),
    AUTH_USER_ROLE_ASSIGNMENT_ERROR("AUTH_USER_ROLE_ASSIGNMENT_ERROR", "사용자에게 할당된 역할이 없어서, 역할 부여를 할 수 없습니다."),
    AUTH_USER_NOT_AUTHORIZED("AUTH_USER_NOT_AUTHORIZED", "사용자의 API 호출 권한이 없습니다."),

    NOT_FOUND_API_NAME_TYPE("NOT_FOUND_API_NAME_TYPE", "URI에 일치하는 value를 찾을 수 없습니다."),
    NOT_ALLOW_APP_NAME("NOT_ALLOW_APP_NAME", "API 호출이 허용되지 않은 APP입니다."),
    NOT_FOUND_APP_NAME("NOT_FOUND_APP_NAME", "찾을 수 없는 APP NAME입니다."),
    NOT_FOUND_AUTHORIZATION_API_PROPERTY("NOT_FOUND_AUTHORIZATION_API_PROPERTY", "일치하는 Properties를 찾을 수 없습니다."),
    NOT_FOUND_REFRESH_TOKEN("NOT_FOUND_REFRESH_TOKEN", "해당 사용자로 저장되어 있는 Refresh Token을 찾을 수 없습니다."),
    NOT_REGISTERED_API_NAME("NOT_REGISTERED_API_NAME", "해당 API NAME이 올바르게 등록되어 있지 않습니다. 서버 개발자에에 문의해 주세요."),

    MISMATCH_REFRESH_TOKEN("MISMATCH_REFRESH_TOKEN", "저장되어 있는 Refresh Token과 값이 일치하지 않습니다."),

    UNKNOWN_EXCEPTION("UNKNOWN_EXCEPTION", "예상치 못한 예외가 발생했습니다. 문제 해결 조치를 위해 author과 컨택하세요.");

    private final String code;
    private final String msg;
}
