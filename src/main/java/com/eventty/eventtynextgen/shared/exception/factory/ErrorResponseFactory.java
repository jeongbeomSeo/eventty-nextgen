package com.eventty.eventtynextgen.shared.exception.factory;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enumtype.ErrorType;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public final class ErrorResponseFactory {
    private final String code;
    private final String msg;
    private final Object detail;

    public static ResponseEntity<ErrorResponse> toResponseEntity(CustomException ex) {
        ErrorType errorType = ex.getErrorType();
        Object detail = ex.getDetail();

        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(ErrorResponse.of(errorType, detail));
    }

    private ErrorResponseFactory(ErrorType errorType, Object detail) {
        this.code = errorType.getCode();
        this.msg = errorType.getMsg();
        this.detail = detail;
    }
}
