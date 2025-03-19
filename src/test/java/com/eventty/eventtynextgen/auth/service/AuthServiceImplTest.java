package com.eventty.eventtynextgen.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.auth.client.AuthClient;
import com.eventty.eventtynextgen.auth.fixture.AuthUserFixture;
import com.eventty.eventtynextgen.auth.fixture.SignupRequestFixture;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import com.eventty.eventtynextgen.auth.repository.JpaAuthRepository;
import com.eventty.eventtynextgen.auth.service.utils.PasswordEncoder;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.AuthErrorType;
import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceImplTest {

    @Mock
    private JpaAuthRepository authRepository;

    @Mock
    private AuthClient authClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    class Signup {

        @Test
        @DisplayName("[GOOD] - 회원 가입에 성공합니다.")
        void 회원가입_성공() {
            // given
            SignupRequest request = SignupRequestFixture.successUserRoleRequest();

            AuthUser authUser = AuthUserFixture.createAuthUserBySignupRequest(request);
            Long id = 1L;

            when(authRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.hashPassword(request.getPassword())).thenReturn("hashed_password");
            when(authRepository.save(any(AuthUser.class))).thenReturn(authUser);
            when(authClient.saveUser(any(UserSignupRequest.class))).thenReturn(id);

            AuthService authService = new AuthServiceImpl(authClient, authRepository, passwordEncoder);

            // when
            Long result = authService.signup(request);

            // then
            assertThat(result).isEqualTo(id);
        }

        @Test
        @DisplayName("[BAD] - 이메일 중복으로 인하여 회원가입에 실패합니다.")
        void 회원가입_실패_이메일_중복() {
            // given
            SignupRequest request = SignupRequestFixture.successUserRoleRequest();

            when(authRepository.existsByEmail(request.getEmail())).thenReturn(true);

            AuthService authService = new AuthServiceImpl(authClient, authRepository, passwordEncoder);

            // when & then
            try {
                authService.signup(request);
            } catch (CustomException customException) {
                assertThat(customException.getErrorType())
                    .isEqualTo(AuthErrorType.EMAIL_ALREADY_EXISTS);
            }
        }

        @Test
        @DisplayName("[BAD] - Api Client 결과 실패로 인해 회원가입에 실패합니다.")
        void 회원가입_실패_클라이언트_호출_결과_실패() {
            // given
            SignupRequest request = SignupRequestFixture.successUserRoleRequest();
            AuthUser authUser = AuthUserFixture.createAuthUserBySignupRequest(request);

            when(authRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.hashPassword(request.getPassword())).thenReturn("hashed_password");
            when(authRepository.save(any(AuthUser.class))).thenReturn(authUser);
            when(authClient.saveUser(any(UserSignupRequest.class))).thenThrow(
                CustomException.badRequest(AuthErrorType.CLIENT_ERROR));

            AuthService authService = new AuthServiceImpl(authClient, authRepository, passwordEncoder);

            // when & then
            try {
                authService.signup(request);
            } catch (CustomException customException) {
                assertThat(customException.getErrorType())
                    .isEqualTo(AuthErrorType.CLIENT_ERROR);
            }
        }

    }

}