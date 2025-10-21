package com.eventty.eventtynextgen.base.exception.advice;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.ErrorType;
import com.eventty.eventtynextgen.base.exception.enums.CommonErrorType;
import com.eventty.eventtynextgen.base.exception.factory.ErrorMsgFactory;
import com.eventty.eventtynextgen.base.exception.factory.ErrorResponseEntityFactory;
import com.eventty.eventtynextgen.base.exception.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        Map<String, String> errorMsg = ErrorMsgFactory.createFieldErrorMsg(fieldError.getField(), fieldError.getDefaultMessage());

        CustomException customException = CustomException.badRequest(CommonErrorType.INVALID_INPUT_DATA, errorMsg);

        loggingError(customException, errorMsg);

        return ErrorResponseEntityFactory.toResponseEntity(customException);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        Map<String, String> errorMsg = ErrorMsgFactory.createFieldErrorMsg(field, message);

        CustomException customException = CustomException.badRequest(CommonErrorType.INVALID_INPUT_DATA, errorMsg);

        loggingError(customException, errorMsg);

        return ErrorResponseEntityFactory.toResponseEntity(customException);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        String errorMsg = ex.getMessage();
        CustomException customException = CustomException.badRequest(CommonErrorType.SQL_CONSTRAINT_VIOLATION, errorMsg);

        loggingError(customException, errorMsg);

        return ErrorResponseEntityFactory.toResponseEntity(customException);
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        loggingError(ex, null);

        return ErrorResponseEntityFactory.toResponseEntity(ex);
    }

    private void loggingError(CustomException customException, Object errorMsg) {
        log.error("http-status={} code={} msg={} detail={}",
            customException.getHttpStatus().value(),
            customException.getErrorType().getCode(),
            customException.getErrorType().getMsg(),
            customException.getDetail() != null ? customException.getDetail() : errorMsg);
    }
}
