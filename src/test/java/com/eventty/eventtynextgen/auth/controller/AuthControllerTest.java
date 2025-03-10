package com.eventty.eventtynextgen.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventty.eventtynextgen.auth.model.dto.request.SignupDTO;
import com.eventty.eventtynextgen.auth.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    class Signup {

        @Test
        @DisplayName("회원 등록에 성공")
        void 회원등록_성공() throws JsonProcessingException {
            // given
            SignupRequest signup = new SignupRequest();
            Long createdId = 100L;

            when(authService.signup(any(SignupRequest.class))).thenReturn(createdId);

            // when
            ResultActions resultActions = mockMvc.perform(
                post("api/v1/auth").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signup));

            // then
            resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", contains("/api/v1/signup/100")));

            verify(authService, times(1)).signup(any(SignupRequest.class));
        }
    }

}