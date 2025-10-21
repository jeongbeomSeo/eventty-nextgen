package com.eventty.eventtynextgen.base.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorType implements ErrorType {

    INVALID_INPUT_DATA("INVALID_INPUT_DATA", "올바르지 않은 입력값이 들어왔습니다."),
    OCCURRED_IO_EXCEPTION("OCCURRED_IO_EXCEPTION", "입출력 예외가 발생했습니다."),
    SQL_CONSTRAINT_VIOLATION("SQL_CONSTRAINT_VIOLATION", "데이터베이스 제약 조건에 위배되는 작업이 수행되었습니다.")
    ;

    private final String code;
    private final String msg;
}
