package com.eventty.eventtynextgen.base.exception;


import com.eventty.eventtynextgen.base.exception.enums.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ErrorType errorType;
    private final Object detail;

    private CustomException(Throwable cause, HttpStatus httpStatus, ErrorType errorType, Object detail) {
        super(cause);
        this.httpStatus = httpStatus;
        this.errorType = errorType;
        this.detail = detail;
    }

    private CustomException(HttpStatus httpStatus, ErrorType errorType, Object detail) {
        super(errorType.getMsg());
        this.httpStatus = httpStatus;
        this.errorType = errorType;
        this.detail = detail;
    }

    public static CustomException of(Throwable cause, HttpStatus httpStatus, ErrorType errorType, Object detail) {
        return new CustomException(cause, httpStatus, errorType, detail);
    }

    public static CustomException of(HttpStatus httpStatus, ErrorType errorType, Object detail) {
        return new CustomException(httpStatus, errorType, detail);
    }

    public static CustomException of(HttpStatus httpStatus, ErrorType errorType) {
        return new CustomException(httpStatus, errorType, "");
    }

    public static CustomException badRequest(ErrorType errorType) {
        return new CustomException(HttpStatus.BAD_REQUEST, errorType, "");
    }

    public static CustomException badRequest(ErrorType errorType, Object detail) {
        return new CustomException(HttpStatus.BAD_REQUEST, errorType, detail);
    }

    public static CustomException badRequest(Throwable cause, ErrorType errorType, Object detail) {
        return new CustomException(cause, HttpStatus.BAD_REQUEST, errorType, detail);
    }
}
