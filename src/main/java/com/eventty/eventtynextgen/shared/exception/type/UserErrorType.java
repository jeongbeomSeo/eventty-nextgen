package com.eventty.eventtynextgen.shared.exception.type;

import com.eventty.eventtynextgen.shared.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorType implements ErrorType {

    AUTH_USER_ID_ALREADY_EXISTS("AUTH_USER_ID_ALREADY_EXISTS", "회원 ID가 이미 존재합니다."),
    ;

    private final String code;
    private final String msg;
}
