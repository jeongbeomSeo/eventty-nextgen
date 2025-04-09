package com.eventty.eventtynextgen.certification;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.certification.request.CertificationExistsRequestCommand;
import com.eventty.eventtynextgen.certification.request.CertificationRequestCommand;
import com.eventty.eventtynextgen.certification.request.CertificationValidateRequestCommand;
import com.eventty.eventtynextgen.certification.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationValidateCodeResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql({"/user_data.sql"})
class CertificationControllerTest {

    private static final String BASE_URL = "/api/v1/user/certification";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("이메일 사용 가능 여부 검사 테스트")
    class Exists {

        private static final String URL = BASE_URL + "/exists";

        @Test
        @DisplayName("DB에 이메일이 존재하지 않을 경우 이메일이 존재하지 않는다고 응답하다.")
        void DB에_이메일이_존재하지_않을_경우_FALSE를_응답하다() throws Exception {
            // given
            String email = "NON_exist@naver.com";
            CertificationExistsRequestCommand certificationExistsRequestCommand = new CertificationExistsRequestCommand(email);

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .content(objectMapper.writeValueAsString(certificationExistsRequestCommand))
                .contentType(APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CertificationExistsResponseView(email, false))));
        }

        @Test
        @DisplayName("DB에 이메일이 존재할 경우 이메일이 존재한다고 응답한다.")
        void DB에_이메일이_존재할_경우_TRUE를_응답한다() throws Exception {
            // given
            String email = "test@naver.com";
            CertificationExistsRequestCommand certificationExistsRequestCommand = new CertificationExistsRequestCommand(email);

            // when
            ResultActions resultActions = mockMvc.perform(get(URL)
                .content(objectMapper.writeValueAsString(certificationExistsRequestCommand))
                .contentType(APPLICATION_JSON));

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
            CertificationRequestCommand certificationRequestCommand = new CertificationRequestCommand(certTarget);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(certificationRequestCommand))
                .contentType(APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(CertificationSendCodeResponseView.createMessage(certTarget, 10))));
        }
    }

    @Sql({"/certification_data.sql"})
    @Nested
    @DisplayName("인증 코드 검사 테스트")
    class ValidateCode {

        private static final String URL = BASE_URL + "/validate";
        private static final String EMAIL = "test@naver.com";
        private static final String CODE = "ABCDEF";
        private static final String EXPIRED_EMAIL = "expired@naver.com";

        private static final String EXPIRE_CODE = "EXPIRE";

        @Test
        @DisplayName("이메일과 인증 코드를 성공적으로 인증할 경우, 코드 인증에 성공했다는 응답을 전달해준다.")
        void 인증_코드를_성공적으로_인증할_경우_코드_인증에_성공했다는_응답을_전달해준다() throws Exception {
            // given
            CertificationValidateRequestCommand certificationValidateRequestCommand = new CertificationValidateRequestCommand(EMAIL, CODE);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(certificationValidateRequestCommand))
                .contentType(APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CertificationValidateCodeResponseView(CODE, true))));
        }

        @Test
        @DisplayName("인증 코드가 만료되었을 경우, 코드 인증에 실패했다는 응답을 전달한다.")
        void 인증_코드가_만료되었을_경우_코드_인증에_실패했다는_응답을_전달한다() throws Exception {
            // given
            CertificationValidateRequestCommand certificationValidateRequestCommand = new CertificationValidateRequestCommand(EXPIRED_EMAIL, EXPIRE_CODE);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(certificationValidateRequestCommand))
                .contentType(APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CertificationValidateCodeResponseView(EXPIRE_CODE, false))));
        }

        @Test
        @DisplayName("이메일과 인증 코드를 찾을 수 없는 경우, 코드 인증에 실패했다는 응답을 전달한다.")
        void 인증_코드를_찾을_수_없는_경우_코드_인증에_실패했다는_응답을_전달한다() throws Exception {
            // given
            CertificationValidateRequestCommand certificationValidateRequestCommand = new CertificationValidateRequestCommand(EMAIL, EXPIRE_CODE);

            // when
            ResultActions resultActions = mockMvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(certificationValidateRequestCommand))
                .contentType(APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CertificationValidateCodeResponseView(EXPIRE_CODE, false))));
        }

    }

}