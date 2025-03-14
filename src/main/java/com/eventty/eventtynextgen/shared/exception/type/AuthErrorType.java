package com.eventty.eventtynextgen.shared.exception.type;

import com.eventty.eventtynextgen.shared.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorType implements ErrorType {

    EMAIL_ALREADY_EXISTS_EXCEPTION("EMAIL_ALREADY_EXISTS_EXCEPTION", "이미 중복된 이메일이 존재합니다."),
    CLIENT_ERROR_EXCEPTION("CLIENT_ERROR_EXCEPTION", "외부 서버에서 예외가 발생했습니다."),

    // Input Validation
    EMAIL_INPUT_EXCEPTION("EMAIL_INPUT_EXCEPTION", "이메일 형식이 올바르지 않습니다: 이메일은 '@'와 '.'가 포함되어 있어야 합니다."),
    PASSWORD_INPUT_EXCEPTION("PASSWORD_INPUT_EXCEPTION", "패스워드 형식이 올바르지 않습니다: 패스워드는 8자 이상 16자 이하여야 합니다."),
    PHONE_INPUT_EXCEPTION("PHONE_INPUT_EXCEPTION", "핸드폰 형식이 올바르지 않습니다: 핸드폰 번호는 000-0000-0000의 형식이어야 합니다."),
    BIRTH_INPUT_EXCEPTION("BIRTH_INPUT_EXCEPTION", "생년월일은 YYYY.MM.DD 혹은 YYYY-MM-DD 형식이어야 합니다."),
    USER_ROLE_INPUT_EXCEPTION("USER_ROLE_INPUT_EXCEPTION", "사용자 역할을 USER 혹은 HOST이어야 합니다.")
    ;

    private final String code;
    private final String msg;
}
