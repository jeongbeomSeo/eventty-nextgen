package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorType implements ErrorType {

    INVALID_INPUT_DATA("INVALID_INPUT_DATA", "올바르지 않은 입력값이 들어왔습니다."),
    ;

    private final String code;
    private final String msg;
}
