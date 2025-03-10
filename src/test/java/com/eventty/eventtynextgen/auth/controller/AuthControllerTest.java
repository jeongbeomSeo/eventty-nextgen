package com.eventty.eventtynextgen.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.auth.model.UserRole;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class Signup {

        @Test
        @DisplayName("회원 등록에 성공")
        void 회원등록_성공() throws Exception {
            // given
            SignupRequest signup = new SignupRequest("test@google.com", "12345678", "000-0000-0000",
                "1990-01-01", UserRole.USER);
            Long createdId = 100L;

            when(authService.signup(any(SignupRequest.class))).thenReturn(createdId);

            // when
            ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth")
                    .content(objectMapper.writeValueAsString(signup))
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", StringEndsWith.endsWith("/api/v1/auth/100")));

            verify(authService, times(1)).signup(any(SignupRequest.class));
        }
    }

}