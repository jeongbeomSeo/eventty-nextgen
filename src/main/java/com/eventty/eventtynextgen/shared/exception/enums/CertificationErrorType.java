package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CertificationErrorType implements ErrorType {

    // Certification Code
    CERTIFICATION_CODE_SAVE_ERROR("CERTIFICATION_CODE_SAVE_ERROR", "인증 코드 저장 중 오류가 발생했습니다."),
    EXPIRE_EMAIL_CERTIFICATION_CODE("EXPIRE_EMAIL_CERTIFICATION_CODE", "이메일 검증 코드가 만료되었습니다."),
    MISMATCH_EMAIL_CERTIFICATION_CODE("MISMATCH_EMAIL_CERTIFICATION_CODE", "이메일 검증 코드가 일치하지 않습니다."),

    // Authentication
    AUTH_PASSWORD_MISMATCH("AUTH_PASSWORD_MISMATCH", "패스워드가 일치하지 않습니다."),
    AUTH_USER_NOT_ACTIVE("AUTH_USER_NOT_ACTIVE", "해당 유저는 활성화 상태가 아닙니다."),

    // Authorization
    AUTH_USER_ROLE_ASSIGNMENT_ERROR("AUTH_USER_ROLE_ASSIGNMENT_ERROR", "사용자에게 할당된 역할이 없어서, 역할 부여를 할 수 없습니다.");


    private final String code;
    private final String msg;
}
