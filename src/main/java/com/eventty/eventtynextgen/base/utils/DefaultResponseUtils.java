package com.eventty.eventtynextgen.base.utils;

import static com.eventty.eventtynextgen.base.constant.BaseConst.OBJECT_MAPPER;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import com.eventty.eventtynextgen.shared.exception.enums.ErrorType;
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
public class DefaultResponseUtils {

    public static void writeErrorResponseToResponse(HttpServletResponse response, HttpStatus status, Throwable ex, ErrorType errorType) {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse errorResponse = ErrorResponse.of(errorType, ex.getMessage());
        try {
            String json = OBJECT_MAPPER.writeValueAsString(errorResponse);
            response.getWriter().write(json);
        } catch (Throwable exception) {
            log.error("Error Response를 response에 담아주는 과정에서 예외가 발생했습니다. ex.msg: {}", exception.getMessage());
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "예외 응답을 생성하는 과정에서 문제가 생겼습니다. 코드 작성자에게 문의하세요.");
        }
    }
}
