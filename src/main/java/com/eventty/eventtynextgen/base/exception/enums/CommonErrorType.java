package com.eventty.eventtynextgen.base.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorType implements ErrorType {

    INVALID_INPUT_DATA("INVALID_INPUT_DATA", "올바르지 않은 입력값이 들어왔습니다."),
    OCCURRED_IO_EXCEPTION("OCCURRED_IO_EXCEPTION", "입출력 예외가 발생했습니다.")
    ;

    private final String code;
    private final String msg;
}
