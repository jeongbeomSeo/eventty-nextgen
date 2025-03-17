package com.eventty.eventtynextgen.shared.handler;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.ErrorType;
import com.eventty.eventtynextgen.shared.exception.type.CommonErrorType;
import com.eventty.eventtynextgen.shared.factory.ErrorMsgFactory;
import com.eventty.eventtynextgen.shared.factory.ErrorResponseFactory;
import com.eventty.eventtynextgen.shared.model.ErrorResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        Map<String, String> errorMsg = ErrorMsgFactory.createFieldErrorMsg(fieldError.getField(),
            fieldError.getDefaultMessage());

        CustomException customException = CustomException.badRequest(
            CommonErrorType.INVALID_INPUT_DATA, errorMsg);

        // TODO: 반복되는 Error logging 작업을 분리 고려중
        log.warn(String.format("http-status={%s} code={%s} msg={%s} detail={%s}",
            customException.getHttpStatus().value(), CommonErrorType.INVALID_INPUT_DATA.getCode(),
            CommonErrorType.INVALID_INPUT_DATA.getMsg(), errorMsg));

        return ErrorResponseFactory.toResponseEntity(customException);
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorType errorType = ex.getErrorType();
        log.warn(String.format("http-status={%s} code={%s} msg={%s} detail={%s}",
            ex.getHttpStatus().value(), errorType.getCode(), errorType.getClass(), ex.getDetail()));

        return ErrorResponseFactory.toResponseEntity(ex);
    }

}
