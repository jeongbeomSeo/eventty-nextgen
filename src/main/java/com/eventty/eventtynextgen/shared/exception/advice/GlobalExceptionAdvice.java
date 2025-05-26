package com.eventty.eventtynextgen.shared.exception.advice;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.ErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.CommonErrorType;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorMsgFactory;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorResponseEntityFactory;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        Map<String, String> errorMsg = ErrorMsgFactory.createFieldErrorMsg(fieldError.getField(), fieldError.getDefaultMessage());

        CustomException customException = CustomException.badRequest(CommonErrorType.INVALID_INPUT_DATA, errorMsg);

        // TODO: 반복되는 Error logging 작업을 분리 고려중
        log.error("http-status={} code={} msg={} detail={}",
            customException.getHttpStatus().value(),
            CommonErrorType.INVALID_INPUT_DATA.getCode(),
            CommonErrorType.INVALID_INPUT_DATA.getMsg(),
            errorMsg);

        return ErrorResponseEntityFactory.toResponseEntity(customException);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        Map<String, String> errorMsg = ErrorMsgFactory.createFieldErrorMsg(field, message);

        CustomException customException = CustomException.badRequest(CommonErrorType.INVALID_INPUT_DATA, errorMsg);

        log.error("http-status={} code={} msg={} detail={}",
            customException.getHttpStatus().value(),
            CommonErrorType.INVALID_INPUT_DATA.getCode(),
            CommonErrorType.INVALID_INPUT_DATA.getMsg(),
            errorMsg
        );

        return ErrorResponseEntityFactory.toResponseEntity(customException);
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorType errorType = ex.getErrorType();
        log.error("http-status={} code={} msg={} detail={}",
            ex.getHttpStatus().value(), errorType.getCode(), errorType.getClass(), ex.getDetail());

        return ErrorResponseEntityFactory.toResponseEntity(ex);
    }

}
