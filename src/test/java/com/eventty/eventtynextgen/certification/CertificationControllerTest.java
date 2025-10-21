package com.eventty.eventtynextgen.certification;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.ErrorResponse;
import com.eventty.eventtynextgen.base.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.base.exception.factory.ErrorResponseEntityFactory;
import com.eventty.eventtynextgen.base.fixture.CertificationTokenFixture;
import com.eventty.eventtynextgen.certification.fixture.CertificationIssueTokenRequestCommandFixture;
import com.eventty.eventtynextgen.certification.request.CertificationIssueTokenRequestCommand;
import com.eventty.eventtynextgen.config.TestcontainersConfiguration;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.CertificationTokenInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Certification Controller 통합 테스트")
class CertificationControllerTest {

    private static final String BASE_URL = "/api/v1/certification";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

            CertificationIssueTokenRequestCommand certificationIssueTokenRequestCommand = CertificationIssueTokenRequestCommandFixture.create(appName,
                appSecret);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificationIssueTokenRequestCommand)));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString(CERTIFICATION_TOKEN_COOKIE_NAME)))
                .andExpect(content().string(Matchers.containsString("tokenType")))
                .andExpect(content().string(Matchers.containsString("tokenValue")));
        }

        @Test
        @DisplayName("API 호출 권한이 없는 서비스일 경우 토큰 발행에 실패한다")
        void API_호출_권한이_없는_사용자일_경우_토큰_발행에_실패한다() throws Exception {
            // given
            String appName = "client99";
            String appSecret = "426825ada56b04d2cbca1333c38fe5f5";

            CertificationIssueTokenRequestCommand certificationIssueTokenRequestCommand = CertificationIssueTokenRequestCommandFixture.create(appName,
                appSecret);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(CertificationErrorType.NOT_ALLOWED_APP_NAME));

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificationIssueTokenRequestCommand)));

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

            CertificationIssueTokenRequestCommand certificationIssueTokenRequestCommand = CertificationIssueTokenRequestCommandFixture.create(appName,
                appSecret);

            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseEntityFactory.toResponseEntity(
                CustomException.badRequest(CertificationErrorType.MISMATCH_SECRET_KEY));

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificationIssueTokenRequestCommand)));

            // then
            resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }
    }
}