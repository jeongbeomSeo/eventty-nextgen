package com.eventty.eventtynextgen.shared.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private HttpStatus httpStatus;

    private ErrorType errorType;

    private CustomException(HttpStatus httpStatus, ErrorType errorType) {
        this.errorType = errorType;
    }

    public static CustomException of(HttpStatus httpStatus, ErrorType errorType) {
        return new CustomException(httpStatus, errorType);
    }

    public static CustomException badRequest(ErrorType errorType) {
        return new CustomException(HttpStatus.BAD_REQUEST, errorType);
    }
}
