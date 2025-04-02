package com.eventty.eventtynextgen.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.user.fixture.SignupRequestFixture;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enumtype.CommonErrorType;
import com.eventty.eventtynextgen.shared.exception.enumtype.UserErrorType;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorMsgFactory;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorResponseFactory;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.request.UserSignupRequestCommand;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.stream.Stream;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String BASE_URL = "/api/v1/user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @Nested
    @DisplayName("회원 가입 테스트")
    class Signup {

        @Test
        @DisplayName("signup test - 이메일이 존재하지 않는 회원가입 요청은 성공한다.")
        void 이메일이_존재하지_않는_경우_회원가입_요청_성공한다() throws Exception {
            // given
            UserSignupRequestCommand signupRequest = SignupRequestFixture.successUserRoleRequest();
            User user = UserFixture.createUserBySignupRequest(signupRequest);

            when(userRepository.existsByEmail(signupRequest.email())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            // when
            ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(new UserSignupResponseView(user.getId()))));
        }

        @Test
        @DisplayName("signup test - 이메일이 존재하는 경우 회원가입 요청은 실패하고 예외를 던진다.")
        void 이메일이_존재하는_경우_회원가입_요청_실패한다() throws Exception {
            // given
            UserSignupRequestCommand signupRequest = SignupRequestFixture.successUserRoleRequest();
            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.EMAIL_ALREADY_EXISTS));

            when(userRepository.existsByEmail(signupRequest.email())).thenReturn(true);

            ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isConflict())
                .andExpect(
                    MockMvcResultMatchers.content()
                        .string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @DisplayName("회원가입 입력값 유효성 검증 테스트")
        @Nested
        class SignupRequestValidationTest {

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("validSignupRequests")
            @DisplayName("request validation - 모든 입력값이 유효한 회원가입 요청은 성공해야 한다.")
            void 회원가입_입력값_유효성_검증에_통과한다(String fixtureName, UserSignupRequestCommand request)
                throws Exception {
                // given
                User user = UserFixture.createUserBySignupRequest(request);

                when(userRepository.existsByEmail(request.email())).thenReturn(false);
                when(userRepository.save(any(User.class))).thenReturn(user);

                // when
                ResultActions resultActions = mockMvc.perform(
                    post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

                // then
                resultActions.andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(new UserSignupResponseView(user.getId()))));
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("invalidSignupRequestsByEmail")
            @DisplayName("request validation - 이메일이 유효하지 않는 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
            void 회원가입_입력값_이메일_검증으로_인해_요청은_실패한다(String fixture, UserSignupRequestCommand request)
                throws Exception {
                // given
                ResponseEntity<ErrorResponse> responseEntity = getErrorResponseResponseEntity(
                    "email",
                    "이메일은 '@'와 '.'가 포함되어 있어야 합니다.");

                // when
                ResultActions resultActions = mockMvc.perform(
                    post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                );

                // then
                resultActions.andExpect(status().isBadRequest())
                    .andExpect(
                        MockMvcResultMatchers.content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("invalidSignupRequestsByPassword")
            @DisplayName("request validation - 패스워드가 유효하지 않은 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
            void 회원가입_입력값_패스워드_검증으로_인해_요청은_실패한다(String fixture, UserSignupRequestCommand request)
                throws Exception {
                // given
                ResponseEntity<ErrorResponse> responseEntity = getErrorResponseResponseEntity(
                    "password",
                    "패스워드는 8자 이상 16자 이하여야 합니다.");

                // when
                ResultActions resultActions = mockMvc.perform(
                    post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                );

                // then
                resultActions.andExpect(status().isBadRequest())
                    .andExpect(
                        MockMvcResultMatchers.content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("invalidSignupRequestByName")
            @DisplayName("request validation - 이름이 NULL이거나 빈 문자열인 요청은 클라이언트에게 실패한 이유가 제공되어야 한다.")
            void 회원가입_입력값_이름_검증으로_인해_요청은_실패한다(String fixture, UserSignupRequestCommand request)
                throws Exception {
                // given
                ResponseEntity<ErrorResponse> responseEntity = getErrorResponseResponseEntity(
                    "name",
                    "이름은 null이거나 빈 문자열일 수 없습니다.");

                // when
                ResultActions resultActions = mockMvc.perform(
                    post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                );

                // then
                resultActions.andExpect(status().isBadRequest())
                    .andExpect(
                        MockMvcResultMatchers.content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
            }

            @Test
            @DisplayName("request validation - 핸드폰 번호 포맷이 유효하지 않은 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
            void 회원가입_입력값_핸드폰_번호_검증으로_인해_요청은_실패한다() throws Exception {
                // given
                UserSignupRequestCommand request = SignupRequestFixture.invalidPhoneNumberRequest();

                ResponseEntity<ErrorResponse> responseEntity = getErrorResponseResponseEntity(
                    "phone",
                    "핸드폰 번호는 000-0000-0000의 형식이어야 합니다.");

                // when
                ResultActions resultActions = mockMvc.perform(
                    post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                );

                // then
                resultActions.andExpect(status().isBadRequest())
                    .andExpect(
                        MockMvcResultMatchers.content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
            }

            @Test
            @DisplayName("request validation - 생년월일 포맷이 유효하지 않은 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
            void 회원가입_입력값_생년월일_검증에_실패한다() throws Exception {
                // given
                UserSignupRequestCommand request = SignupRequestFixture.invalidBirthdateFormatRequest();

                ResponseEntity<ErrorResponse> responseEntity = getErrorResponseResponseEntity(
                    "birth",
                    "생년월일은 YYYY.MM.DD 혹은 YYYY-MM-DD 형식이어야 합니다.");

                // when
                ResultActions resultActions = mockMvc.perform(
                    post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                );

                // then
                resultActions.andExpect(status().isBadRequest())
                    .andExpect(
                        MockMvcResultMatchers.content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
            }

            @Test
            @DisplayName("request validation - 사용자 역할이 올바르지 않은 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
            void 회원가입_입력값_사용자역할_검증에_실패한다() throws Exception {
                // given
                UserSignupRequestCommand request = SignupRequestFixture.invalidUserRoleRequest();

                ResponseEntity<ErrorResponse> responseEntity = getErrorResponseResponseEntity(
                    "userRole",
                    "사용자 역할을 USER 혹은 HOST이어야 합니다.");

                // when
                ResultActions resultActions = mockMvc.perform(
                    post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                );

                // then
                resultActions.andExpect(status().isBadRequest())
                    .andExpect(
                        MockMvcResultMatchers.content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
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

            private static Stream<Arguments> invalidSignupRequestByName() {
                return Stream.of(
                    Arguments.of("이름이 null인 request",
                        SignupRequestFixture.nameIsNullRequest()),
                    Arguments.of("이름이 빈 문자열인 request",
                        SignupRequestFixture.nameIsEmptyRequest())
                );
            }

            private ResponseEntity<ErrorResponse> getErrorResponseResponseEntity(String field,
                String msg) {
                Map<String, String> errorMsg = ErrorMsgFactory.createFieldErrorMsg(field,
                    msg);
                CustomException customException = CustomException.badRequest(
                    CommonErrorType.INVALID_INPUT_DATA, errorMsg);
                return ErrorResponseFactory.toResponseEntity(
                    customException);
            }
        }
    }

}
