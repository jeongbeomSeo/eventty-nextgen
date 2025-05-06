package com.eventty.eventtynextgen.shared.exception;

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

    public static ErrorResponse of(String code, String msg) {
        return new ErrorResponse(code, msg, null);
    }

    public static ErrorResponse of(String code, String msg, Object detail) {
        return new ErrorResponse(code, msg, detail);
    }
}
