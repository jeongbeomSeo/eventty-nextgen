package com.eventty.eventtynextgen.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import com.eventty.eventtynextgen.user.utils.PasswordEncoder;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.User.UserStatus;
import com.eventty.eventtynextgen.user.fixture.SignupRequestFixture;
import com.eventty.eventtynextgen.user.fixture.UpdateRequestFixture;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.eventty.eventtynextgen.user.request.UserChangePasswordRequestCommand;
import com.eventty.eventtynextgen.user.request.UserSignUpRequestCommand;
import com.eventty.eventtynextgen.user.request.UserUpdateRequestCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("User Controller 통합 테스트")
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
        @DisplayName("이메일이 존재하지 않는 경우 회원가입 요청은 `성공`한다.")
        void 이메일이_존재하지_않는_경우_회원가입_요청_성공한다() throws Exception {
            // given
            UserSignUpRequestCommand signupRequest = SignupRequestFixture.successUserSignUpRequest(email);

            // when
            ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty());
        }

        @Test
        @DisplayName("이메일이 존재하는 경우 회원가입 요청은 `실패하고 예외를 전달`한다.")
        void 이메일이_존재하는_경우_회원가입_요청_실패한다() throws Exception {
            // given
            UserSignUpRequestCommand signupRequest = SignupRequestFixture.successUserSignUpRequest(email);
            User user = UserFixture.createUserByEmail(email);
            User userFromDb = userRepository.save(user);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.EMAIL_ALREADY_EXISTS));

            // when
            ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isConflict())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));

            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("이미 삭제되어 있는 계정이 존재할 경우 회원가입 요청은 `실패하고 예외를 전달`한다.")
        void 이미_삭제되어_있는_계정이_존재할_경우_요청에_실패하고_예외를_전달한다() throws Exception {
            // given
            UserSignUpRequestCommand signupRequest = SignupRequestFixture.successUserSignUpRequest(email);
            User user = UserFixture.createUserByEmail(email);
            user.updateDeleteStatus(UserStatus.DELETED);
            User userFromDb = userRepository.save(user);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED));

            // when
            ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));

            userRepository.delete(userFromDb);
        }

        @DisplayName("회원가입 입력값 유효성 검증 테스트")
        @Nested
        class SignupRequestValidationTest {

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("validSignupRequests")
            @DisplayName("request validation - 모든 입력값이 유효한 회원가입 요청은 `성공`해야 한다.")
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
                    .andExpect(jsonPath("$.userId").isNotEmpty())
                    .andExpect(jsonPath("$.email").isNotEmpty());
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("invalidSignupRequestsByEmail")
            @DisplayName("request validation - 이메일이 유효하지 않는 요청은 클라이언트에게 `실패한 이유가 제공` 되어야 한다.")
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
                        content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("invalidSignupRequestsByPassword")
            @DisplayName("request validation - 패스워드가 유효하지 않은 요청은 클라이언트에게 `실패한 이유가 제공` 되어야 한다.")
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
                        content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
            }

            @ParameterizedTest(name = "[{index}] {0}")
            @MethodSource("invalidSignupRequestByName")
            @DisplayName("request validation - 이름이 NULL이거나 빈 문자열인 요청은 클라이언트에게 `실패한 이유가 제공`되어야 한다.")
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
                        content()
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
                        content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
            }

            @Test
            @DisplayName("request validation - 생년월일 포맷이 유효하지 않은 요청은 클라이언트에게 `실패한 이유가 제공` 되어야 한다.")
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
                        content()
                            .string(objectMapper.writeValueAsString(responseEntity.getBody())));
            }

            @Test
            @DisplayName("request validation - 사용자 역할이 올바르지 않은 요청은 클라이언트에게 `실패한 이유가 제공` 되어야 한다.")
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
                        content()
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

    @Nested
    @DisplayName("회원 수정 테스트")
    class update {

        @Test
        @DisplayName("삭제되어 있지 않는 회원 수정 요청은 `성공`한다.")
        void 삭제되어_있지_않는_회원_수정_요청은_성공한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            User userFromDb = userRepository.save(user);

            UserUpdateRequestCommand successUpdateRequest = UpdateRequestFixture.createSuccessUpdateRequest(userFromDb.getId());

            // when
            ResultActions resultActions = mockMvc.perform(patch(BASE_URL)
                .content(objectMapper.writeValueAsString(successUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.phone").isNotEmpty())
                .andExpect(jsonPath("$.birth").isNotEmpty());

            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("존재하지 않은 회원의 수정 요청은 `실패`한다.")
        void 존재하지_않는_회원의_수정_요청은_실패한다() throws Exception {
            // given
            UserUpdateRequestCommand successUpdateRequest = UpdateRequestFixture.createSuccessUpdateRequest(1L);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.NOT_FOUND_USER));

            // when
            ResultActions resultActions = mockMvc.perform(patch(BASE_URL)
                .content(objectMapper.writeValueAsString(successUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isNotFound())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("이미 삭제된 회원 수정 요청은 `실패`한다.")
        void 삭제된_회원_수정_요청은_실패한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            user.updateDeleteStatus(UserStatus.DELETED);
            User userFromDb = userRepository.save(user);
            UserUpdateRequestCommand successUpdateRequest = UpdateRequestFixture.createSuccessUpdateRequest(userFromDb.getId());

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED));

            // when
            ResultActions resultActions = mockMvc.perform(patch(BASE_URL)
                .content(objectMapper.writeValueAsString(successUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));

            userRepository.delete(userFromDb);
        }
    }

    @Nested
    @DisplayName("회원 삭제 테스트")
    class Delete {

        @Test
        @DisplayName("삭제되지 않은 회원의 삭제 요청은 `성공`한다.")
        void 삭제되지_않은_회원의_삭제_요청은_성공한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            User userFromDb = userRepository.save(user);

            // when
            ResultActions resultActions = mockMvc.perform(delete(BASE_URL)
                .param("user-id", String.valueOf(userFromDb.getId())));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").isNotEmpty());

            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("존재하지 않는 회원의 삭제 요청은 `실패`한다.")
        void 존재하지_않는_회원_삭제_요청은_실패한다() throws Exception {
            // given
            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.NOT_FOUND_USER));

            // when
            ResultActions resultActions = mockMvc.perform(delete(BASE_URL)
                .param("user-id", String.valueOf(1L)));

            // then
            resultActions.andExpect(status().isNotFound())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("이미 삭제된 회원의 삭제 요청은 `실패`한다.")
        void 이미_삭제된_회원의_삭제_요청은_실패한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            user.updateDeleteStatus(UserStatus.DELETED);
            User userFromDb = userRepository.save(user);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED));

            // when
            ResultActions resultActions = mockMvc.perform(delete(BASE_URL)
                .param("user-id", String.valueOf(userFromDb.getId())));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));

            userRepository.delete(userFromDb);
        }
    }

    @Nested
    @DisplayName("회원 상태 활성화 테스트")
    class ActivateDeletedUser {

        @Test
        @DisplayName("삭제된 회원일 경우 활성화 요청에 `성공`한다.")
        void 삭제된_회원일_경우_활성화_요청예_성공한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            user.updateDeleteStatus(UserStatus.DELETED);
            User userFromDb = userRepository.save(user);

            String url = BASE_URL + "/" + userFromDb.getId() + "/status";

            // when
            ResultActions resultActions = mockMvc.perform(patch(url));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userFromDb.getId()))
                .andExpect(jsonPath("$.email").value(userFromDb.getEmail()))
                .andExpect(jsonPath("$.name").value(userFromDb.getName()));

            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("삭제되지 않은 회원일 경우 활성화 요청에 `실패`한다.")
        void 삭제되지_않은_회원일_경우_활성화_요청에_실패한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            User userFromDb = userRepository.save(user);

            String url = BASE_URL + "/" + userFromDb.getId() + "/status";

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.USER_NOT_DELETED));

            // when
            ResultActions resultActions = mockMvc.perform(patch(url));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));

            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("존재하지 않는 회원일 경우 활성화 요청에 `실패`한다.")
        void 존재하지_않는_회원일_경우_활성화_요청에_실패한다() throws Exception {
            // given
            String url = BASE_URL + "/" + "1" + "/status";

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.NOT_FOUND_USER));

            // when
            ResultActions resultActions = mockMvc.perform(patch(url));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }
    }

    @Nested
    @DisplayName("이름과 핸드폰 번호로 회원 이메일 찾기")
    class FindEmail {

        private static final String NAME = "홍길동";
        private static final String PHONE = "010-0000-0000";

        @BeforeEach
        void cleanup() {
            jdbcTemplate.update("DELETE FROM users WHERE name = ? AND phone = ?", NAME, PHONE);
        }

        private final String URL = BASE_URL + "/email";

        @Test
        @DisplayName("요청으로 들어온 데이터를 통해 1개의 계정을 찾을 경우 요청에 `성공`한다.")
        void 요청으로_들어온_데이터를_통해_1개의_계정을_찾을_경우_요청에_성공한다() throws Exception {
            // given
            User user = UserFixture.createUserByNameAndPhone(NAME, PHONE);
            User userFromDb = userRepository.save(user);

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .param("name", NAME)
                .param("phone", PHONE));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmailInfos.size()").value(1))
                .andExpect(jsonPath("$.userEmailInfos[*].userId").isNotEmpty())
                .andExpect(jsonPath("$.userEmailInfos[*].email").isNotEmpty());
            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("요청으로 들어온 데이터를 통해 모든 활성화 계정 찾아서 요청에 `모든 계정의 정보를 담아서 반환`한다.")
        void 요청으로_들어온_데이터를_통해_2개_이상의_계정을_찾을_경우_요청에_성공한다() throws Exception {
            // given
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                String email = "test" + i + "@naver.com";
                User user = UserFixture.createUserByEmailAndNameAndPhone(email, NAME, PHONE);
                User userFromDb = userRepository.save(user);

                users.add(userFromDb);
            }

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .param("name", NAME)
                .param("phone", PHONE));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmailInfos.size()").value(5))
                .andExpect(jsonPath("$.userEmailInfos[*].userId").isNotEmpty())
                .andExpect(jsonPath("$.userEmailInfos[*].email").isNotEmpty());

            userRepository.deleteAll(users);
        }

        @Test
        @DisplayName("요청으로 들어온 데이터를 통해 3개의 활성화 계정과 2개의 삭제된 계정을 찾을 경우 요청에 `3개의 계정만 반환`한다.")
        void 요청으로_들어온_데이터를_통해_3개의_활성화_계정과_2개의_삭제된_계정을_찾을_경우_요청에_성공한다() throws Exception {
            // given
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                String email = "test" + i + "@naver.com";
                User user = UserFixture.createUserByEmailAndNameAndPhone(email, NAME, PHONE);
                if (i >= 3) {
                    user.updateDeleteStatus(UserStatus.DELETED);
                }
                User userFromDb = userRepository.save(user);

                users.add(userFromDb);
            }

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .param("name", NAME)
                .param("phone", PHONE));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmailInfos.size()").value(3))
                .andExpect(jsonPath("$.userEmailInfos[*].userId").isNotEmpty())
                .andExpect(jsonPath("$.userEmailInfos[*].email").isNotEmpty());

            userRepository.deleteAll(users);
        }

        @Test
        @DisplayName("요청으로 들어온 데이터를 통해 0개의 계정을 찾을 경우 요청에 `성공`한다.")
        void 요청으로_들어온_데이터를_통해_0개의_계정을_찾을_경우_요청에_성공한다() throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .param("name", NAME)
                .param("phone", PHONE));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmailInfos.size()").value(0));
        }
    }

    @Nested
    @DisplayName("화원 비밀번호 변경")
    class ChangePassword {

        private static final String URL = BASE_URL + "/password";

        @Test
        @DisplayName("현재 비밀번호 매칭 검증과 변경 비밀번호 확인 검증에 통과할 경우 요청에 `성공`한다.")
        void 현재_비밀번호_매칭_검증과_변경_비밀번호_확인_검증에_통과할_경우_요청에_성공한다() throws Exception {
            // given
            String currentPassword = "currentPassword";
            String encodedCurrentPassword = PasswordEncoder.encode(currentPassword);
            User user = UserFixture.createUserByPassword(encodedCurrentPassword);
            User userFromDb = userRepository.save(user);
            String updatedPassword = "updatedPassword";

            UserChangePasswordRequestCommand userChangePasswordRequestCommand = new UserChangePasswordRequestCommand(userFromDb.getId(), currentPassword,
                updatedPassword, updatedPassword);

            // when
            ResultActions result = mockMvc.perform(patch(URL)
                .content(objectMapper.writeValueAsString(userChangePasswordRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userFromDb.getId()))
                .andExpect(jsonPath("$.name").value(userFromDb.getName()))
                .andExpect(jsonPath("$.email").value(userFromDb.getEmail()));

            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("현재 비밀번호 매칭 검증에 실패할 경우 요청에 `실패`한다.")
        void 현재_비밀번호_매칭_검증에_실패할_경우_요청에_실패한다() throws Exception {
            // given
            String currentPassword = "currentPassword";
            String encodedCurrentPassword = PasswordEncoder.encode(currentPassword);
            User user = UserFixture.createUserByPassword(encodedCurrentPassword);
            User userFromDb = userRepository.save(user);
            String updatedPassword = "updatedPassword";

            UserChangePasswordRequestCommand userChangePasswordRequestCommand = new UserChangePasswordRequestCommand(userFromDb.getId(), "mismatchPassword",
                updatedPassword, updatedPassword);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.MISMATCH_CURRENT_PASSWORD));

            // when
            ResultActions result = mockMvc.perform(patch(URL)
                .content(objectMapper.writeValueAsString(userChangePasswordRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));

            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("변경 비밀번호 확인 검증에 실패할 경우 요청에 `실패`한다.")
        void 변경_비밀번호_확인_검증에_실패할_경우_요청에_실패한다() throws Exception {
            // given
            String currentPassword = "currentPassword";
            String encodedCurrentPassword = PasswordEncoder.encode(currentPassword);
            User user = UserFixture.createUserByPassword(encodedCurrentPassword);
            User userFromDb = userRepository.save(user);
            String updatedPassword = "updatedPassword";

            UserChangePasswordRequestCommand userChangePasswordRequestCommand = new UserChangePasswordRequestCommand(userFromDb.getId(), currentPassword,
                updatedPassword, updatedPassword + "2");

            // when
            ResultActions result = mockMvc.perform(patch(URL)
                .content(objectMapper.writeValueAsString(userChangePasswordRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.msg").isNotEmpty())
                .andExpect(jsonPath("$.detail").isNotEmpty());

            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("삭제된 계정일 경우 요청에 `실패`한다.")
        void 삭제된_계정일_경우_요청에_실패한다() throws Exception {
            // given
            String currentPassword = "currentPassword";
            String encodedCurrentPassword = PasswordEncoder.encode(currentPassword);
            User user = UserFixture.createUserByPassword(encodedCurrentPassword);
            user.updateDeleteStatus(UserStatus.DELETED);
            User userFromDb = userRepository.save(user);
            String updatedPassword = "updatedPassword";

            UserChangePasswordRequestCommand userChangePasswordRequestCommand = new UserChangePasswordRequestCommand(userFromDb.getId(), currentPassword,
                updatedPassword, updatedPassword);

            // when
            ResultActions result = mockMvc.perform(patch(URL)
                .content(objectMapper.writeValueAsString(userChangePasswordRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.msg").isNotEmpty());

            userRepository.delete(userFromDb);
        }

        @Test
        @DisplayName("비밀번호를 변경하고자 하는 계정을 찾을 수 없는 경우 요청에 `실패`한다.")
        void 계정을_찾을_수_없는_경우_요청에_실패한다() throws Exception {
            // given
            String currentPassword = "currentPassword";
            String updatedPassword = "updatedPassword";

            UserChangePasswordRequestCommand userChangePasswordRequestCommand = new UserChangePasswordRequestCommand(1L, currentPassword,
                updatedPassword, updatedPassword);

            // when
            ResultActions result = mockMvc.perform(patch(URL)
                .content(objectMapper.writeValueAsString(userChangePasswordRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.msg").isNotEmpty());
        }
    }
}
