package com.eventty.eventtynextgen.shared.exception;

import com.eventty.eventtynextgen.shared.exception.enumtype.ErrorType;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String code;
    private final String msg;
    private final Object detail;

    private ErrorResponse(String code, String msg, Object detail) {
        this.code = code;
        this.msg = msg;
        this.detail = detail;
    }

    public static ErrorResponse of(ErrorType errorType, Object detail) {
        return new ErrorResponse(errorType.getCode(), errorType.getMsg(), detail);
    }

}
