package com.eventty.eventtynextgen.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.user.auth.client.AuthClient;
import com.eventty.eventtynextgen.auth.fixture.AuthUserFixture;
import com.eventty.eventtynextgen.auth.fixture.SignupRequestFixture;
import com.eventty.eventtynextgen.user.model.request.SignupRequest;
import com.eventty.eventtynextgen.user.auth.model.entity.AuthUser;
import com.eventty.eventtynextgen.user.auth.repository.JpaAuthRepository;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.AuthErrorType;
import com.eventty.eventtynextgen.shared.factory.ErrorResponseFactory;
import com.eventty.eventtynextgen.shared.model.ErrorResponse;
import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthClient authClient;

    @MockitoBean
    private JpaAuthRepository authRepository;

    @Nested
    @DisplayName("회원 가입 테스트")
    class Signup {
        @Test
        @DisplayName("signup test - 이메일이 존재하지 않는 회원가입 요청은 성공한다.")
        void 이메일이_존재하지_않는_경우_회원가입_요청_성공한다() throws Exception {
            // given
            SignupRequest signupRequest = SignupRequestFixture.successUserRoleRequest();
            AuthUser authUser = AuthUserFixture.createAuthUserBySignupRequest(signupRequest);
            Long userId = 100L;

            when(authRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
            when(authRepository.save(any(AuthUser.class))).thenReturn(authUser);
            when(authClient.saveUser(any(UserSignupRequest.class))).thenReturn(userId);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/auth")
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header()
                    .string("Location", StringEndsWith.endsWith("/api/v1/auth/100")));
        }

        @Test
        @DisplayName("signup test - 이메일이 존재하는 경우 회원가입 요청은 실패하고 예외를 던진다.")
        void 이메일이_존재하는_경우_회원가입_요청_실패한다() throws Exception {
            // given
            SignupRequest signupRequest = SignupRequestFixture.successUserRoleRequest();
            ResponseEntity<ErrorResponse> responseEntity = ErrorResponseFactory.toResponseEntity(
                CustomException.badRequest(AuthErrorType.EMAIL_ALREADY_EXISTS));

            when(authRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

            // when
            ResultActions resultActions = mockMvc.perform(post("/api/v1/auth")
                .content(objectMapper.writeValueAsString(signupRequest))
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isConflict())
                .andExpect(
                    MockMvcResultMatchers.content()
                        .string(objectMapper.writeValueAsString(responseEntity.getBody())));
        }
    }
}
