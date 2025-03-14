package com.eventty.eventtynextgen.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.auth.fixture.SignupRequestFixture;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("validSignupRequests")
        @DisplayName("signup request validation - 모든 입력값이 유효한 회원가입 요청은 성공해야 한다.")
        void 회원가입_입력값_유효성_검증에_통과한다(String fixtureName, SignupRequest signupRequest)
            throws Exception {
            // given
            Long createdId = 100L;

            when(authService.signup(any(SignupRequest.class))).thenReturn(createdId);

            // when
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth")
                    .content(objectMapper.writeValueAsString(signupRequest))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated())
                .andExpect(
                    header().string("Location", StringEndsWith.endsWith("/api/v1/auth/100")));

            verify(authService, times(1)).signup(any(SignupRequest.class));
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidSignupRequestsByEmail")
        @DisplayName("signup request validation - 이메일이 유효하지 않는 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
        void 회원가입_입력값_이메일_검증에_실패한다(String fixture, SignupRequest signupRequest)
            throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth")
                    .content(objectMapper.writeValueAsString(signupRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    MockMvcResultMatchers.content().json(EmailErrorInEnum));
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidSignupRequestsByPassword")
        @DisplayName("signup request validation - 패스워드가 유효하지 않는 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
        void 회원가입_입력값_패스워드_검증에_실패한다(String fixture, SignupRequest signupRequest)
            throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth")
                    .content(objectMapper.writeValueAsString(signupRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    MockMvcResultMatchers.content().json(passwordErrorInEnum));
        }

        @Test
        @DisplayName("signup request validation - 생년월일이 유효하지 않는 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
        void 회원가입_입력값_생년월일_검증에_실패한다() throws Exception {
            // given
            SignupRequest signupRequest = SignupRequestFixture.invalidBirthdateFormatRequest();

            // when
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth")
                    .content(objectMapper.writeValueAsString(signupRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    MockMvcResultMatchers.content().json(birthdateFormatErrorInEnum));
        }

        @Test
        @DisplayName("signup request validation - 사용자 역할이 올바르지 않은 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
        void 회원가입_입력값_사용자역할_검증에_실패한다() throws Exception {
            // given
            SignupRequest signupRequest = SignupRequestFixture.invalidBirthdateFormatRequest();

            // when
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth")
                    .content(objectMapper.writeValueAsString(signupRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    MockMvcResultMatchers.content().json(userRoleErrorInEnum));
        }

        private static Stream<Arguments> validSignupRequests() {
            return Stream.of(
                Arguments.of("USER 역할을 가진 성공적인 Request",
                    SignupRequestFixture.successUserRoleRequest()),
                Arguments.of("HOST 역할을 가진 성공적인 Request",
                    SignupRequestFixture.successHostRoleRequest())
            );
        }

        private static Stream<Arguments> invalidSignupRequestsByEmail() {
            return Stream.of(
                Arguments.of("이메일에 @가 빠져있는 Request",
                    SignupRequestFixture.missingAtSymbolInEmailRequest()),
                Arguments.of("이메일에 .가 빠져있는 Request",
                    SignupRequestFixture.missingDotInEmailRequest())
            );
        }

        private static Stream<Arguments> invalidSignupRequestsByPassword() {
            return Stream.of(
                Arguments.of("패스워드가 8자 미만인 Request",
                    SignupRequestFixture.shortPasswordRequest()),
                Arguments.of("패스워드가 16자 초과인 Request",
                    SignupRequestFixture.longPasswordRequest())
            );
        }
    }

}