package com.eventty.eventtynextgen.base.utils;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.ErrorType;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtilsImpl implements ResponseUtils {

    @Override
    public void writeErrorResponseToResponse(HttpServletResponse response, CustomException ex) {
        writeErrorResponseToResponse(response, ex.getHttpStatus(), ex, ex.getErrorType());
    }

    @Override
    public void writeErrorResponseToResponse(HttpServletResponse response, HttpStatus status, Throwable ex, ErrorType errorType) {
        DefaultResponseUtils.writeErrorResponseToResponse(response, status, ex, errorType);
    }
}
