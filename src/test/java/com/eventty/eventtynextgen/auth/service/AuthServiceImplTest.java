package com.eventty.eventtynextgen.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.eventty.eventtynextgen.auth.model.UserRole;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import com.eventty.eventtynextgen.auth.repository.AuthRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceImplTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ApiClient apiClient;

    @Nested
    class Signup {

        @Test
        @DisplayName("[GOOD] - 회원 가입에 성공합니다.")
        void 회원가입_성공() {
            // given
            SignupRequest request = new SignupRequest("email@mm.mm", "12345678", "010-0000-0000",
                "2000-01-01", UserRole.USER);

            AuthUser authUser = new AuthUser(request.getEmail(), request.getPassword(), request.getUserRole());
            Long id = 1L;

            when(authRepository.save()).thenReturn(authUser);
            when(apiClient.saveUser(authUser)).thenReturn(id);

            // when
            Long id = authService.signup(signupRequest);

            // then
            assertThat(result).equals(id);
        }
    }

}