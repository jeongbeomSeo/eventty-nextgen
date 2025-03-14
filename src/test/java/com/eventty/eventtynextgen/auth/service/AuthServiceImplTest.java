package com.eventty.eventtynextgen.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.auth.client.AuthClient;
import com.eventty.eventtynextgen.auth.model.UserRole;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import com.eventty.eventtynextgen.auth.repository.JpaAuthRepository;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.AuthErrorType;
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
    private AuthServiceImpl authService;

    @Mock
    private JpaAuthRepository authRepository;

    @Mock
    private AuthClient authClient;

    @Nested
    class Signup {

        @Test
        @DisplayName("[GOOD] - 회원 가입에 성공합니다.")
        void 회원가입_성공() {
            // given
            SignupRequest request = createRequest();

            AuthUser authUser = createAuthUserByRequest(request);
            Long id = 1L;

            when(authRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(authRepository.save(any(AuthUser.class))).thenReturn(authUser);
            when(authClient.saveUser(authUser)).thenReturn(id);

            // when
            Long result = authService.signup(request);

            // then
            assertThat(result).isEqualTo(id);
        }

        @Test
        @DisplayName("[BAD] - 이메일 중복으로 인하여 회원가입에 실패합니다.")
        void 회원가입_실패_이메일_중복() {
            // given
            SignupRequest request = createRequest();

            when(authRepository.existsByEmail(request.getEmail())).thenReturn(true);

            // when & then
            try {
                authService.signup(request);
            } catch (CustomException customException) {
                assertThat(customException.getErrorType())
                    .isEqualTo(AuthErrorType.EMAIL_ALREADY_EXISTS_EXCEPTION);
            }
        }

        @Test
        @DisplayName("[BAD] - Api Client 결과 실패로 인해 회원가입에 실패합니다.")
        void 회원가입_실패_클라이언트_호출_결과_실패() {
            // given
            SignupRequest request = createRequest();
            AuthUser authUser = createAuthUserByRequest(request);

            when(authRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(authRepository.save(any(AuthUser.class))).thenReturn(authUser);
            when(authClient.saveUser(authUser)).thenThrow(
                CustomException.badRequest(AuthErrorType.CLIENT_ERROR_EXCEPTION));

            // when & then
            try {
                authService.signup(request);
            } catch (CustomException customException) {
                assertThat(customException.getErrorType())
                    .isEqualTo(AuthErrorType.CLIENT_ERROR_EXCEPTION);
            }
        }

        private AuthUser createAuthUserByRequest(SignupRequest request) {
            return new AuthUser(1L, request.getEmail(), request.getPassword(),
                request.getUserRole());
        }

        private SignupRequest createRequest() {
            return new SignupRequest("email@mm.mm", "12345678", "010-0000-0000",
                "2000-01-01", UserRole.USER);
        }
    }

}