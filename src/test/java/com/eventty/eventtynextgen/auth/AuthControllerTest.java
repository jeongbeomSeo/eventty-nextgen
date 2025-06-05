package com.eventty.eventtynextgen.auth;

import static com.eventty.eventtynextgen.shared.constant.HttpHeaderConst.AUTHORIZATION_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.eventty.eventtynextgen.auth.constant.AuthConst;
import com.eventty.eventtynextgen.base.constant.BaseConst;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.SessionTokenInfo;
import com.eventty.eventtynextgen.auth.provider.TestJwtTokenProvider;
import com.eventty.eventtynextgen.auth.core.Authentication;
import com.eventty.eventtynextgen.auth.fixture.AuthenticationFixture;
import com.eventty.eventtynextgen.auth.refreshtoken.RefreshTokenRepository;
import com.eventty.eventtynextgen.auth.refreshtoken.entity.RefreshToken;
import com.eventty.eventtynextgen.certification.request.CertificationIssueCertificationTokenRequestCommand;
import com.eventty.eventtynextgen.auth.request.AuthLoginRequestCommand;
import com.eventty.eventtynextgen.auth.request.AuthReissueSessionTokenRequestCommand;
import com.eventty.eventtynextgen.auth.shared.utils.CookieUtils;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import com.eventty.eventtynextgen.shared.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.JwtTokenErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorResponseEntityFactory;
import com.eventty.eventtynextgen.shared.utils.DateUtils;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.User.UserStatus;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.eventty.eventtynextgen.user.utils.PasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class AuthControllerTest {

    private static final String BASE_URL = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("로그인 테스트")
    class Login {

        private static final String URL = BASE_URL + "/login";

        @Test
        @DisplayName("사용자 자격 증명에 성공할 경우 로그인에 성공한다")
        void 사용자_자격_증명에_성공할_경우_로그인에_성공한다() throws Exception {
            // given
            String email = "test@gmail.com";
            String plainPassword = "testpassword";
            User user = UserFixture.createUserByCredentials(email, PasswordEncoder.encode(plainPassword));
            User userFromDb = userRepository.save(user);

            AuthLoginRequestCommand loginRequest = new AuthLoginRequestCommand(email, plainPassword);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userFromDb.getId()))
                .andExpect(jsonPath("$.email").value(userFromDb.getEmail()))
                .andExpect(jsonPath("$.accessTokenInfo.tokenType").value(BaseConst.JWT_TOKEN_TYPE))
                .andExpect(jsonPath("$.accessTokenInfo.accessToken").isNotEmpty())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("refreshToken=")));
        }

        @Test
        @DisplayName("로그인 아이디인 이메일이 잘못된 경우 로그인에 실패한다")
        void 로그인_아이디인_이메일이_잘못된_경우_로그인에_실패한다() throws Exception {
            // given
            String email = "test@gmai.com";
            String plainPassword = "testpassword";
            User user = UserFixture.createUserByCredentials(email, PasswordEncoder.encode(plainPassword));
            userRepository.save(user);

            AuthLoginRequestCommand loginRequest = new AuthLoginRequestCommand("wrong@gmail.com", plainPassword);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.NOT_FOUND_USER)
            );

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("로그인 아이디에 해당하는 계정의 패스워드가 불일치할 경우 로그인에 실패한다")
        void 로그인_아이디에_해당하는_계정의_패스워드가_불일치할_경우_로그인에_실패한다() throws Exception {
            // given
            String email = "test@gmai.com";
            String plainPassword = "testpassword";
            User user = UserFixture.createUserByCredentials(email, PasswordEncoder.encode(plainPassword));
            userRepository.save(user);

            AuthLoginRequestCommand loginRequest = new AuthLoginRequestCommand(email, "wrongpassword");

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(AuthErrorType.AUTH_PASSWORD_MISMATCH)
            );

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("삭제된 사용자로 로그인을 시도할 경우 로그인에 실패한다")
        void 삭제된_사용자로_로그인을_시도할_경우_로그인에_실패한다() throws Exception {
            // given
            String email = "test@gmai.com";
            String plainPassword = "testpassword";
            User user = UserFixture.createUserByCredentials(email, PasswordEncoder.encode(plainPassword));
            user.updateDeleteStatus(UserStatus.DELETED);
            userRepository.save(user);

            AuthLoginRequestCommand loginRequest = new AuthLoginRequestCommand(email, plainPassword);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(AuthErrorType.AUTH_USER_NOT_ACTIVE)
            );

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class Logout {

        private static final String URL = BASE_URL + "/logout";

        // TODO: LoginFilter를 구현하고 Aspect가 정상 동작 가능할 시점에 테스트 가능
/*        @Test
        @DisplayName("로그인한 사용자가 로그아웃을 시도할 경우 로그아웃에 성공한다")
        void 로그인한_사용자가_로그아웃을_시도할_경우_로그아웃에_성공한다() throws Exception {
            // given
            String email = "test@gmail.com";
            String plainPassword = "testpassword";
            User user = UserFixture.createUserByCredentials(email, PasswordEncoder.encode(plainPassword));
            User userFromDb = userRepository.save(user);
            Authentication authorizedAuthentication = AuthenticationFixture.createAuthorizedLoginIdPasswordAuthentication(userFromDb.getId(),
                email, plainPassword);

            // TODO: 수정
            SessionTokenInfo sessionTokenInfo = JwtTokenProvider.createSessionToken(
                authorizedAuthentication.getUserDetails().getUserId(),
                AuthConst.ACCESS_TOKEN_VALIDITY_IN_MIN,
                AuthConst.REFRESH_TOKEN_VALIDITY_IN_MIN
            );

            RefreshToken refreshToken = RefreshToken.of(sessionTokenInfo.getRefreshToken(), userFromDb.getId(), DateUtils.convertFormatToLocalDateTime(sessionTokenInfo.getRefreshTokenExpiredAt()));
            refreshTokenRepository.save(refreshToken);

            String accessTokenHeader = BaseConst.JWT_TOKEN_TYPE + " " + sessionTokenInfo.getAccessToken();

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .header(AUTHORIZATION_HEADER, accessTokenHeader));

            // then
            resultActions.andExpect(status().isOk());
        }*/

        @Test
        @DisplayName("로그인되어 있지 않은 사용자가 로그아웃을 시도할 경우 로그아웃에 실패한다")
        void 로그인되어_있지_않은_사용자가_로그아웃을_시도할_경우_로그아웃에_실패한다() throws Exception {
            // given
            String email = "test@gmail.com";
            String plainPassword = "testpassword";
            User user = UserFixture.createUserByCredentials(email, PasswordEncoder.encode(plainPassword));
            userRepository.save(user);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL));

            // then
            resultActions.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("액세스 토큰 재발급 테스트")
    class ReissueSessionToken {

        private static final String URL = BASE_URL + "/reissue/session-token";

        @AfterEach
        void tearDown() {
            refreshTokenRepository.deleteAllInBatch();
            userRepository.deleteAllInBatch();
        }

        @Test
        @DisplayName("저장되어 있는 유효한 Refresh Token을 통해 재발급 요청시 재발급에 성공한다")
        void 저장되어_있는_유효한_리프래시_토큰을_통해_재발급_요청시_재발급에_성공한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            User userFromDb = userRepository.save(user);
            Long userId = userFromDb.getId();

            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60L * 60 * 1000, 60L * 60 * 1000);
            String expiredAccessToken = sessionToken.getAccessToken();
            String refreshToken = sessionToken.getRefreshToken();
            refreshTokenRepository.save(RefreshToken.of(refreshToken, userId, LocalDateTime.now().plusDays(7)));

            AuthReissueSessionTokenRequestCommand certificationReissueRequestCommand = new AuthReissueSessionTokenRequestCommand(expiredAccessToken);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .header(CookieUtils.REFRESH_TOKEN_HEADER_NAME, refreshToken)
                .content(objectMapper.writeValueAsString(certificationReissueRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.accessTokenInfo.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessTokenInfo.accessToken").isNotEmpty());
        }

        // TODO: 만료된 토큰 테스트와 DB에 저장되어 있는 토큰 정보 만료 테스트 둘 다 체크
        @Test
        @DisplayName("만료된 Refresh Token을 통해 재발급 요청시 재발급에 실패한다")
        void 만료된_리프래시_토큰을_통해_재발급_요청시_재발급에_실패한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            User userFromDb = userRepository.save(user);
            Long userId = userFromDb.getId();

            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60L * 60 * 1000, -60L * 60 * 1000);
            String expiredAccessToken = sessionToken.getAccessToken();
            String expiredRefreshToken = sessionToken.getRefreshToken();
            refreshTokenRepository.save(RefreshToken.of(expiredRefreshToken, userId, LocalDateTime.now().minusDays(7)));

            AuthReissueSessionTokenRequestCommand certificationReissueRequestCommand = new AuthReissueSessionTokenRequestCommand(expiredAccessToken);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(JwtTokenErrorType.EXPIRED_TOKEN));

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .header(CookieUtils.REFRESH_TOKEN_HEADER_NAME, expiredRefreshToken)
                .content(objectMapper.writeValueAsString(certificationReissueRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        // TODO: 현재로써 잘못된 서명으로 만들어진 토큰을 생성하는 방법이 확정되지 않음
/*        @Test
        @DisplayName("토큰 서명 겁증에 실패할 경우 재발급에 실패한다")
        void 토큰_서명_검증에_실패할_경우_재발급에_실패한다() throws Exception{
            // given
            User user = UserFixture.createUser();
            User userFromDb = userRepository.save(user);
            Long userId = userFromDb.getId();

            String accessToken = testJwtTokenProvider.createExpiredAccessToken();
            String refreshToken = testJwtTokenProvider.createRefreshTokenWithInvalidSignature();
            refreshTokenRepository.save(RefreshToken.of(refreshToken, userId, LocalDateTime.now().plusDays(7)));

            AuthReissueSessionTokenRequestCommand certificationReissueRequestCommand = new AuthReissueSessionTokenRequestCommand(accessToken);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(JwtTokenErrorType.FAILED_SIGNATURE_VALIDATION));

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .header(CookieUtils.REFRESH_TOKEN_HEADER_NAME, refreshToken)
                .content(objectMapper.writeValueAsString(certificationReissueRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }*/

        @Test
        @DisplayName("올바르지 않은 형식으로 구성된 Refresh Token을 통하여 재발급 요청시 재발급에 실패한다")
        void 올바르지_않은_형식으로_구성된_리프래시_토큰을_통하여_재발급_요청시_재발급에_실패한다() throws Exception{
            // given
            User user = UserFixture.createUser();
            User userFromDb = userRepository.save(user);
            Long userId = userFromDb.getId();

            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60L * 60 * 1000, 60L * 60 * 1000);
            String accessToken = sessionToken.getAccessToken();
            String refreshToken = "Illegal_state_refresh_token";
            refreshTokenRepository.save(RefreshToken.of(refreshToken, userId, LocalDateTime.now().plusDays(7)));

            AuthReissueSessionTokenRequestCommand certificationReissueRequestCommand = new AuthReissueSessionTokenRequestCommand(accessToken);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(JwtTokenErrorType.ILLEGAL_STATE_TOKEN));

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .header(CookieUtils.REFRESH_TOKEN_HEADER_NAME, refreshToken)
                .content(objectMapper.writeValueAsString(certificationReissueRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("저장되어 있지 않은 Refresh Token을 통해 재발급 요청시 재발급에 실패한다")
        void 저장되어_있지_않은_리프래시_토큰을_통해_재발급_요청시_재발급에_실패한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            User userFromDb = userRepository.save(user);
            Long userId = userFromDb.getId();

            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60L * 60 * 1000, 60L * 60 * 1000);
            String accessToken = sessionToken.getAccessToken();
            String refreshToken = sessionToken.getRefreshToken();

            AuthReissueSessionTokenRequestCommand certificationReissueRequestCommand = new AuthReissueSessionTokenRequestCommand(accessToken);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(AuthErrorType.NOT_FOUND_REFRESH_TOKEN));

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .header(CookieUtils.REFRESH_TOKEN_HEADER_NAME, refreshToken)
                .content(objectMapper.writeValueAsString(certificationReissueRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("저장되어 있는 Refresh Token과 값이 일치하지 않은 경우 재발급에 실패한다")
        void 저장되어_있는_리프래시_토큰과_값이_일치하지_않을_경우_재발급에_실패한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            User userFromDb = userRepository.save(user);
            Long userId = userFromDb.getId();

            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60L * 60 * 1000, 60L * 60 * 1000);
            String accessToken = sessionToken.getAccessToken();
            String refreshToken = sessionToken.getRefreshToken();
            refreshTokenRepository.save(RefreshToken.of(refreshToken, userId, LocalDateTime.now().plusDays(7)));

            String differentRefreshToken = JwtTokenProvider.createSessionToken(userId, -60L * 60 * 1000, 120L * 60 * 1000)
                .getRefreshToken();

            AuthReissueSessionTokenRequestCommand certificationReissueRequestCommand = new AuthReissueSessionTokenRequestCommand(accessToken);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(AuthErrorType.MISMATCH_REFRESH_TOKEN));

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .header(CookieUtils.REFRESH_TOKEN_HEADER_NAME, differentRefreshToken)
                .content(objectMapper.writeValueAsString(certificationReissueRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("사용자가 삭제된 상태로 변경된 경우 재발급에 실패한다")
        void 사용자가_삭제된_상태로_변경된_경우_재발급에_실패한다() throws Exception {
            // given
            User user = UserFixture.createUser();
            user.updateDeleteStatus(UserStatus.DELETED);
            User userFromDb = userRepository.save(user);

            Long userId = userFromDb.getId();
            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60L * 60 * 1000, 60L * 60 * 1000);
            String accessToken = sessionToken.getAccessToken();
            String refreshToken = sessionToken.getRefreshToken();
            refreshTokenRepository.save(RefreshToken.of(refreshToken, userId, LocalDateTime.now().plusDays(7)));

            AuthReissueSessionTokenRequestCommand certificationReissueRequestCommand = new AuthReissueSessionTokenRequestCommand(accessToken);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED));

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .header(CookieUtils.REFRESH_TOKEN_HEADER_NAME, refreshToken)
                .content(objectMapper.writeValueAsString(certificationReissueRequestCommand))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(
                    content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }
    }
}