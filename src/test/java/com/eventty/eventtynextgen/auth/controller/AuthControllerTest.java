package com.eventty.eventtynextgen.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.auth.model.UserRole;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class Signup {

        @Test
        @DisplayName("회원 등록에 성공")
        void 회원등록_성공() throws Exception {
            // given
            SignupRequest signup = new SignupRequest("test@google.com", "12345678", "000-0000-0000",
                "1990-01-01", UserRole.USER);
            Long createdId = 100L;

            when(authService.signup(any(SignupRequest.class))).thenReturn(createdId);

            // when
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth")
                    .content(objectMapper.writeValueAsString(signup))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
              resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", StringEndsWith.endsWith("/api/v1/auth/100")));

            verify(authService, times(1)).signup(any(SignupRequest.class));
        }

        @ParameterizedTest
        @MethodSource("invalidSignupRequest")
        @DisplayName("회원 등록 실패 - 입력 데이터 유효성 검증")
        void 회원등록_실패_입력데이터_유효성_검증_실패(String email, String password, String phone, String birth, UserRole userRole) throws Exception {
            // given
            SignupRequest signupRequest = new SignupRequest(email, password, phone, birth,
                userRole);
            Long createdId = 100L;

            // when
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth")
                    .content(objectMapper.writeValueAsString(signupRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpect(status().is4xxClientError());
        }

        private static Stream<Arguments> invalidSignupRequest() {
            return Stream.of(
                Arguments.of("email", "12345678", "000-0000-0000", "1999-01-01", UserRole.USER),
                Arguments.of("email@mm.mm", "1234", "000-0000-0000", "1999-01-01", UserRole.USER),
                Arguments.of("email@mm.mm", "12345678", "0000-0000", "1999-01-01", UserRole.USER),
                Arguments.of("email@mm.mm", "12345678", "000-0000-0000", "199-01", UserRole.USER),
                Arguments.of("email@mm.mm", "12345678", "000-0000-0000", "1999-99-99", UserRole.USER),
                Arguments.of("email@mm.mm", "12345678", "000-0000-0000", "1999-01-01", null)
            );
        }
    }

}