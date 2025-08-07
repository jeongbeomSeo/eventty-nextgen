package com.eventty.eventtynextgen.base.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventty.eventtynextgen.base.exception.ErrorResponse;
import com.eventty.eventtynextgen.base.exception.enums.CommonErrorType;
import com.eventty.eventtynextgen.base.exception.enums.ErrorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

@DisplayName("Default Response Utility Class 단위 테스트")
class DefaultResponseUtilsTest {

    @Nested
    @DisplayName("Response body에 Error Response 쓰기 테스트")
    class WriteErrorResponseToResponse {
        @Test
        @DisplayName("httpStatus와 errorType, details를 인자로 받아서 Error Response를 만든 후 Response 인자의 body에 담는다.")
        void httpStatus_errorType_details를_인자로_받아서_Error_Response를_만든_후_Response_인자의_body에_담는다() throws JsonProcessingException, UnsupportedEncodingException {
            // given
            MockHttpServletResponse response = new MockHttpServletResponse();
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
            ErrorType errorType = CommonErrorType.INVALID_INPUT_DATA;
            String details = "invalid input data";

            ErrorResponse errorResponse = ErrorResponse.of(errorType, details);
            ObjectMapper objectMapper = new ObjectMapper();

            // when
            DefaultResponseUtils.writeErrorResponseToResponse(response, httpStatus, errorType, details);

            // then
            assertThat(response.getStatus()).isEqualTo(httpStatus.value());
            assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(errorResponse));
        }
    }
}