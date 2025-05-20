package com.eventty.eventtynextgen.certification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.eventty.eventtynextgen.certification.constant.CertificationConst;
import com.eventty.eventtynextgen.certification.request.CertificationLoginRequestCommand;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.ErrorResponse;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.shared.exception.factory.ErrorResponseEntityFactory;
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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class CertificationControllerTest {

    private static final String BASE_URL = "/api/v1/certification";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();
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

            CertificationLoginRequestCommand loginRequest = new CertificationLoginRequestCommand(email, plainPassword);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userFromDb.getId()))
                .andExpect(jsonPath("$.email").value(userFromDb.getEmail()))
                .andExpect(jsonPath("$.accessTokenInfo.tokenType").value(CertificationConst.JWT_TOKEN_TYPE))
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

            CertificationLoginRequestCommand loginRequest = new CertificationLoginRequestCommand("wrong@gmail.com", plainPassword);

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

            CertificationLoginRequestCommand loginRequest = new CertificationLoginRequestCommand(email, "wrongpassword");

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(CertificationErrorType.AUTH_PASSWORD_MISMATCH)
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

            CertificationLoginRequestCommand loginRequest = new CertificationLoginRequestCommand(email, plainPassword);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(CertificationErrorType.AUTH_USER_NOT_ACTIVE)
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
}