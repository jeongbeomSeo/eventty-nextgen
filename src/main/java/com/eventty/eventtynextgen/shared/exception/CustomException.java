package com.eventty.eventtynextgen.shared.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final Object detail;

    private CustomException(HttpStatus httpStatus, String errorCode, Object detail) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public static CustomException of(HttpStatus httpStatus, String errorCode, Object detail) {
        return new CustomException(httpStatus, errorCode, detail);
    }

    public static CustomException of(HttpStatus httpStatus, String errorCode) {
        return new CustomException(httpStatus, errorCode, "");
    }

    public static CustomException badRequest(String errorCode) {
        return new CustomException(HttpStatus.BAD_REQUEST, errorCode, "");
    }

    public static CustomException badRequest(String errorCode, Object detail) {
        return new CustomException(HttpStatus.BAD_REQUEST, errorCode, detail);
    }
}
