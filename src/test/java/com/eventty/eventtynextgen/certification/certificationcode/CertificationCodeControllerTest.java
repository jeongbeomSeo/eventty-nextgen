package com.eventty.eventtynextgen.certification.certificationcode;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.eventty.eventtynextgen.certification.certificationcode.entity.CertificationCode;
import com.eventty.eventtynextgen.certification.certificationcode.fixture.CertificationCodeFixture;
import com.eventty.eventtynextgen.certification.certificationcode.repository.CertificationCodeRepository;
import com.eventty.eventtynextgen.certification.certificationcode.request.CertificationSendCodeRequestCommand;
import com.eventty.eventtynextgen.certification.certificationcode.request.CertificationValidateCodeRequestCommand;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationValidateCodeResponseView;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class CertificationCodeControllerTest {

    private static final String BASE_URL = "/api/v1/certification";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CertificationCodeRepository certificationCodeRepository;

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
        certificationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Nested
    @DisplayName("이메일 사용 가능 여부 검사 테스트")
    class Exists {

        private static final String URL = BASE_URL + "/exists";

        @Test
        @DisplayName("DB에 이메일이 존재하지 않을 경우 이메일이 존재하지 않는다고 응답하다.")
        void DB에_이메일이_존재하지_않을_경우_FALSE를_응답하다() throws Exception {
            // given
            String email = "NON_exist@naver.com";

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .param("email", email));
            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CertificationExistsResponseView(email, false))));
        }

        @Test
        @DisplayName("DB에 이메일이 존재할 경우 이메일이 존재한다고 응답한다.")
        void DB에_이메일이_존재할_경우_TRUE를_응답한다() throws Exception {
            // given
            String email = "test@naver.com";
            User user = UserFixture.createUserByEmail(email);
            userRepository.save(user);

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .param("email", email));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CertificationExistsResponseView(email, true))));
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 발송 요청 테스트")
    class SendCode {

        private static final String URL = BASE_URL;

        @Test
        @DisplayName("인증 코드를 성공적으로 발송했다면, 사용자에게 메시지를 전달해준다.")
        void 인증_코드를_성공적으로_발생했다면_사용자에게_인증_코드를_전달해준다() throws Exception {
            // given
            String certTarget = "test@naver.com";
            CertificationSendCodeRequestCommand certificationRequestCommand = new CertificationSendCodeRequestCommand(certTarget);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(certificationRequestCommand))
                .contentType(APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(CertificationSendCodeResponseView.createMessage(certTarget, 10))));
        }
    }

    @Nested
    @DisplayName("인증 코드 검사 테스트")
    class ValidateCode {

        private static final String URL = BASE_URL + "/validate";

        @Test
        @DisplayName("이메일과 인증 코드를 성공적으로 인증할 경우, 코드 인증에 성공했다는 응답을 전달해준다.")
        void 인증_코드를_성공적으로_인증할_경우_코드_인증에_성공했다는_응답을_전달해준다() throws Exception {
            // given
            String email = "email@naver.com";
            String code = "ABCDEF";
            int ttl = 10;

            CertificationCode certificationCode = CertificationCodeFixture.createCertificationCode(email, code, ttl);
            certificationCodeRepository.save(certificationCode);

            CertificationValidateCodeRequestCommand certificationValidateRequestCommand = new CertificationValidateCodeRequestCommand(email, code);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(certificationValidateRequestCommand))
                .contentType(APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CertificationValidateCodeResponseView(code, true))));
        }

        @Test
        @DisplayName("만료 기간이 지난 인증 코드를 인증하려고 시도할 경우, 코드 인증에 실패했다는 응답을 전달한다.")
        void 만료_기간이_지난_인증_코드를_인증하려고_시도할_경우_실패했다는_응답을_전달한다() throws Exception {
            // given
            String email = "email@naver.com";
            String code = "ABCDEF";
            int ttl = 0;

            CertificationCode expiredCertificationCode = CertificationCodeFixture.createCertificationCode(email, code, ttl);
            certificationCodeRepository.save(expiredCertificationCode);
            Thread.sleep(500);

            CertificationValidateCodeRequestCommand certificationValidateRequestCommand = new CertificationValidateCodeRequestCommand(email, code);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(certificationValidateRequestCommand))
                .contentType(APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CertificationValidateCodeResponseView(code, false))));
        }

        @Test
        @DisplayName("이메일과 인증 코드를 찾을 수 없는 경우, 코드 인증에 실패했다는 응답을 전달한다.")
        void 인증_코드를_찾을_수_없는_경우_코드_인증에_실패했다는_응답을_전달한다() throws Exception {
            // given
            String email = "email@naver.com";
            String code = "ABCDEF";
            CertificationValidateCodeRequestCommand certificationValidateRequestCommand = new CertificationValidateCodeRequestCommand(email, code);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(certificationValidateRequestCommand))
                .contentType(APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CertificationValidateCodeResponseView(code, false))));
        }

    }

}