package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorType implements ErrorType {

    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "이미 중복된 이메일이 존재합니다."),
    NOT_FOUND_USER("NOT_FOUND_USER", "해당 사용자를 찾을 수 없습니다."),
    USER_SAVE_ERROR("USER_SAVE_ERROR", "사용자 저장 중 오류가 발생했습니다."),
    USER_ALREADY_DELETED("USER_ALREADY_DELETED", "삭제되어 있는 유저입니다."),
    USER_NOT_DELETED("USER_NOT_DELETED", "삭제되어 있지 않은 유저입니다."),

    // Input Validation
    INVALID_EMAIL_FORMAT("INVALID_EMAIL_FORMAT", "이메일 형식이 올바르지 않습니다: 이메일은 '@'와 '.'가 포함되어 있어야 합니다."),
    INVALID_PASSWORD_LENGTH("INVALID_PASSWORD_LENGTH",
        "패스워드 형식이 올바르지 않습니다: 패스워드는 8자 이상 16자 이하여야 합니다."),
    INVALID_PHONE_FORMAT("INVALID_PHONE_FORMAT",
        "핸드폰 형식이 올바르지 않습니다: 핸드폰 번호는 000-0000-0000의 형식이어야 합니다."),
    INVALID_BIRTHDATE_FORMAT("INVALID_BIRTHDATE_FORMAT",
        "생년월일은 YYYY.MM.DD 혹은 YYYY-MM-DD 형식이어야 합니다."),
    INVALID_USER_ROLE("INVALID_USER_ROLE", "사용자 역할을 USER 혹은 HOST이어야 합니다."),

    EXPIRE_EMAIL_VERIFICATION_CODE("EXPIRE_EMAIL_VERIFICATION",
        "이메일 검증 코드가 만료되었습니다."),
    MISMATCH_EMAIL_VERIFICATION_CODE("MISMATCH_EMAIL_VERIFICATION_CODE",
        "이메일 검증 코드가 일치하지 않습니다."),
    ;

    private final String code;
    private final String msg;
}
