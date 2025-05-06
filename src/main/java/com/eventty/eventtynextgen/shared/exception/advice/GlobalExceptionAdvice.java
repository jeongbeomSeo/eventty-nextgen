package com.eventty.eventtynextgen.shared.exception.advice;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorResponseFactory;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorMsgFactory;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {

    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        Map<String, String> errorMsg = ErrorMsgFactory.createFieldErrorMsg(fieldError.getField(), fieldError.getDefaultMessage());

        CustomException customException = CustomException.badRequest("INVALID_INPUT_DATA", errorMsg);
        ResponseEntity<ErrorResponse> responseEntity = errorResponseFactory.toResponseEntity(customException);

        // TODO: 반복되는 Error logging 작업을 분리 고려중
        log.warn(String.format("http-status={%s} body={$s}",
            customException.getHttpStatus().value(),
            responseEntity.getBody()));

        return responseEntity;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        Map<String, String> errorMsg = ErrorMsgFactory.createFieldErrorMsg(field, message);

        CustomException customException = CustomException.badRequest("INVALID_INPUT_DATA", errorMsg);
        ResponseEntity<ErrorResponse> responseEntity = errorResponseFactory.toResponseEntity(customException);

        // TODO: 반복되는 Error logging 작업을 분리 고려중
        log.warn(String.format("http-status={%s} body={$s}",
            customException.getHttpStatus().value(),
            responseEntity.getBody()));

        return responseEntity;
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.warn(String.format("http-status={%s} code={%s} msg={%s} detail={%s}",
            ex.getHttpStatus().value(), ex.getErrorCode(), ex.getMessage(), ex.getDetail()));

        return errorResponseFactory.toResponseEntity(ex);
    }

}
