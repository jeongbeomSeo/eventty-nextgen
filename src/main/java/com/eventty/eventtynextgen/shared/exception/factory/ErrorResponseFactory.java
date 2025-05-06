package com.eventty.eventtynextgen.shared.exception.factory;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import com.eventty.eventtynextgen.shared.exception.factory.properties.ErrorTypeProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorResponseFactory {

    private final ErrorTypeProperties errorTypeProperties;

    public ResponseEntity<ErrorResponse> toResponseEntity(CustomException ex) {
        String msg = errorTypeProperties.getMessage(ex.getHttpStatus(), ex.getErrorCode());
        return ResponseEntity
            .status(ex.getHttpStatus().value())
            .body(ErrorResponse.of(ex.getErrorCode(), msg, ex.getDetail()));
    }
}
