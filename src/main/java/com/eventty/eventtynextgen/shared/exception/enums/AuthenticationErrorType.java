package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationErrorType implements ErrorType {

    AUTH_PASSWORD_MISMATCH("AUTH_PASSWORD_MISMATCH", "패스워드가 일치하지 않습니다."),
    AUTH_USER_NOT_ACTIVE("AUTH_USER_NOT_ACTIVE", "해당 유저는 활성화 상태가 아닙니다.")
    ;

    private final String code;
    private final String msg;
}
