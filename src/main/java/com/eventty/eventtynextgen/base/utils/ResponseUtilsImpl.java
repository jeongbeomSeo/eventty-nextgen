package com.eventty.eventtynextgen.base.utils;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.ErrorType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtilsImpl implements ResponseUtils {

    @Override
    public void writeErrorResponseToResponse(HttpServletResponse response, CustomException ex) {
        writeErrorResponseToResponse(response, ex.getHttpStatus(), ex.getErrorType(), ex.getDetail());
    }

    @Override
    public void writeErrorResponseToResponse(HttpServletResponse response, HttpStatus status, ErrorType errorType, Object details) {
        DefaultResponseUtils.writeErrorResponseToResponse(response, status, errorType, details);
    }
}
