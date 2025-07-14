package com.eventty.eventtynextgen.certification;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.ErrorResponse;
import com.eventty.eventtynextgen.base.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.base.exception.factory.ErrorResponseEntityFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Certification Controller 통합 테스트")
class CertificationControllerTest {

    private static final String BASE_URL = "/api/v1/certification";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("API 호출 권한인 Certification Token 발행 통합 테스트")
    class IssueCertificationToken {

        private static final String URL = BASE_URL + "/issue/certification-token";

        @Test
        @DisplayName("API 호출 권한을 얻은 서비스가 올바른 요청을 보낼 경우 토큰 발행에 성공한다.")
        void API_호출_권한을_얻은_사용자가_올바른_요청을_보낼_경우_토큰_발행에_성공한다() throws Exception {
            // given
            String appName = "client1";
            String appSecret = "426825ada56b04d2cbca1333c38fe5f5";

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .param("appName", appName)
                .param("appSecret", appSecret));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString(CERTIFICATION_TOKEN_COOKIE_NAME)));
        }

        @Test
        @DisplayName("API 호출 권한이 없는 서비스일 경우 토큰 발행에 실패한다")
        void API_호출_권한이_없는_사용자일_경우_토큰_발행에_실패한다() throws Exception {
            // given
            String appName = "client99";
            String appSecret = "426825ada56b04d2cbca1333c38fe5f5";

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(CertificationErrorType.NOT_ALLOWED_APP_NAME));

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .param("appName", appName)
                .param("appSecret", appSecret));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }

        @Test
        @DisplayName("API 호출 권한을 얻은 서비스가 잘못된 키값을 보낸 경우 토큰 발행에 실패한다")
        void API_호출_권한을_얻은_서비스가_잘못된_키값을_보낸_경우_토큰_발행에_실패한다() throws Exception {
            // given
            String appName = "client1";
            String appSecret = "wrongSecretKey";

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(CertificationErrorType.MISMATCH_SECRET_KEY));

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .param("appName", appName)
                .param("appSecret", appSecret));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }
    }
}