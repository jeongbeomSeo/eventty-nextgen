package com.eventty.eventtynextgen.shared.exception.type;

import com.eventty.eventtynextgen.shared.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorType implements ErrorType {

    EMAIL_ALREADY_EXISTS_EXCEPTION("EMAIL_ALREADY_EXISTS_EXCEPTION", "이미 중복된 이메일이 존재합니다."),
    CLIENT_ERROR_EXCEPTION("CLIENT_ERROR_EXCEPTION", "외부 서버에서 예외가 발생했습니다."),
    ;

    private final String code;
    private final String msg;
}
