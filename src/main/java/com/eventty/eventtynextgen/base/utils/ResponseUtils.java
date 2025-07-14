package com.eventty.eventtynextgen.base.utils;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.ErrorType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

public interface ResponseUtils {

    void writeErrorResponseToResponse(HttpServletResponse response, CustomException ex);

    void writeErrorResponseToResponse(HttpServletResponse response, HttpStatus status, ErrorType errorType, Object details);
}
