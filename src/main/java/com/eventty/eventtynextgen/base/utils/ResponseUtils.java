package com.eventty.eventtynextgen.base.utils;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import com.eventty.eventtynextgen.shared.exception.enums.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@UtilityClass
public class ResponseUtils {

    public static void writeErrorResponseToResponse(HttpServletResponse response, CustomException ex) {
        writeErrorResponseToResponse(response, ex.getHttpStatus(), ex, ex.getErrorType());
    }

    public static void writeErrorResponseToResponse(HttpServletResponse response, HttpStatus status, Throwable ex, ErrorType errorType) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = ErrorResponse.of(errorType, ex.getMessage());
        try {
            String json = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(json);
        } catch (Throwable exception) {
            log.error("Error Response를 response에 담아주는 과정에서 예외가 발생했습니다. ex.msg: {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "ResponseUtils의 author과 컨택해야 합니다.");
        }
    }
}
