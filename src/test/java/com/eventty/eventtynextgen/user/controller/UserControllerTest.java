package com.eventty.eventtynextgen.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import com.eventty.eventtynextgen.shared.exception.enums.CommonErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorMsgFactory;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorResponseFactory;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.fixture.SignupRequestFixture;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.eventty.eventtynextgen.user.request.UserSignUpRequestCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class UserControllerTest {

    private static final String BASE_URL = "/api/v1/user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final DBConfiguration config = DBConfigurationBuilder.newBuilder()
        .setPort(13306)
        .setDataDir(new File(".embedded/mariadb"))
        .build();

    private static final DB db;

    static {
        System.out.println("초기화 시작: static 영역");
        try {
            db = DB.newEmbeddedDB(config);
            System.out.println("DB 시작 전");
            db.start();
            System.out.println("DB 시작 후");
            db.createDB("eventty-nextgen", "root", "");
            Thread.sleep(2000);
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:13306/eventty-nextgen", "root", "");
            System.out.println("DB 연결 성공");
            conn.close();
        } catch (ManagedProcessException | InterruptedException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("회원 가입 테스트")
    class Signup {

        private static final String email = "test@naver.com";

        @BeforeEach
        void cleanup() {
            jdbcTemplate.update("DELETE FROM users WHERE email = ?", email);
        }


        @Test
        @DisplayName("signup test - 이메일이 존재하지 않는 경우 회원가입 요청은 성공한다.")
        void 이메일이_존재하지_않는_경우_회원가입_요청_성공한다() throws Exception {
            // given
            UserSignUpRequestCommand signupRequest = SignupRequestFixture.successUserSignUpRequest(email);

            // when
            ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").isNotEmpty());
        }

        @Test
        @DisplayName("signup test - 이메일이 존재하는 경우 회원가입 요청은 실패하고 예외를 던진다.")
        void 이메일이_존재하는_경우_회원가입_요청_실패한다() throws Exception {
            // given
            UserSignUpRequestCommand signupRequest = SignupRequestFixture.successUserSignUpRequest(email);
            User user = UserFixture.createUserByEmail(email);
            userRepository.save(user);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.EMAIL_ALREADY_EXISTS));

            // when
            ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isConflict())
                .andExpect(
                    MockMvcResultMatchers.content()
                        .string(objectMapper.writeValueAsString(responseEntity.getBody())));

            userRepository.delete(user);
        }

        @DisplayName("회원가입 입력값 유효성 검증 테스트")
        @Nested
        class SignupRequestValidationTest {

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("validSignupRequests")
            @DisplayName("request validation - 모든 입력값이 유효한 회원가입 요청은 성공해야 한다.")
            void 회원가입_입력값_유효성_검증에_통과한다(String fixtureName, UserSignUpRequestCommand request)
                throws Exception {
                // given

                // when
                ResultActions resultActions = mockMvc.perform(
                    post(BASE_URL)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));

                // then
                resultActions.andExpect(status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.userId").isNotEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").isNotEmpty());
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("invalidSignupRequestsByEmail")
            @DisplayName("request validation - 이메일이 유효하지 않는 요청은 클라이언트에게 실패한 이유가 제공 되어야 한다.")
            void 회원가입_입력값_이메일_검증으로_인해_요청은_실패한다(String fixture, UserSignUpRequestCommand request)
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
            void 회원가입_입력값_패스워드_검증으로_인해_요청은_실패한다(String fixture, UserSignUpRequestCommand request)
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
            void 회원가입_입력값_이름_검증으로_인해_요청은_실패한다(String fixture, UserSignUpRequestCommand request)
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
                UserSignUpRequestCommand request = SignupRequestFixture.invalidPhoneNumberRequest(email);

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
                UserSignUpRequestCommand request = SignupRequestFixture.invalidBirthdateFormatRequest(email);

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
                UserSignUpRequestCommand request = SignupRequestFixture.invalidUserRoleRequest(email);

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
                        SignupRequestFixture.successUserSignUpRequest(email)),
                    Arguments.of("HOST 역할을 가진 성공적인 Request",
                        SignupRequestFixture.successHostRoleRequest(email))
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
                        SignupRequestFixture.shortPasswordRequest(email)),
                    Arguments.of("패스워드가 16자 초과인 Request",
                        SignupRequestFixture.longPasswordRequest(email))
                );
            }

            private static Stream<Arguments> invalidSignupRequestByName() {
                return Stream.of(
                    Arguments.of("이름이 null인 request",
                        SignupRequestFixture.nameIsNullRequest(email)),
                    Arguments.of("이름이 빈 문자열인 request",
                        SignupRequestFixture.nameIsEmptyRequest(email))
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
