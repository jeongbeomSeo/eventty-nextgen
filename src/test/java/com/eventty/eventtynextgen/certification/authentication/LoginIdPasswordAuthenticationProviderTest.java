package com.eventty.eventtynextgen.certification.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetailsService;
import com.eventty.eventtynextgen.certification.fixture.AuthenticationFixture;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.AuthenticationErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.user.utils.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginIdPasswordAuthenticationProvider 단위 테스트")
class LoginIdPasswordAuthenticationProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Nested
    @DisplayName("사용자 인증")
    class Authenticate {

        @Test
        @DisplayName("모든 제약 조건이 통과할 경우 인증에 성공한 토큰을 발급해준다.")
        void 모든_제약_조건이_통과할_경우_인증에_성공한_토큰을_발행해준다() {
            // given
            Authentication authentication = AuthenticationFixture.createUnauthenticatedLoginIdPasswordAuthentication();

            UserDetails userDetails = mock(UserDetails.class);

            when(userDetailsService.loadUserDetailsByLoginId(authentication.getUserDetails().getLoginId())).thenReturn(userDetails);
            when(userDetails.getPassword()).thenReturn(PasswordEncoder.encode(authentication.getUserDetails().getPassword()));
            when(userDetails.isActive()).thenReturn(true);

            LoginIdPasswordAuthenticationProvider loginIdPasswordAuthenticationProvider = new LoginIdPasswordAuthenticationProvider(userDetailsService);

            // when
            Authentication result = loginIdPasswordAuthenticationProvider.authenticate(authentication);

            // then
            assertThat(result.isAuthenticated()).isTrue();
            assertThat(result.getAuthorities().isEmpty()).isTrue();
        }

        @Test
        @DisplayName("인자 객체의 패스워드와 사용자 패스워드가 일치하지 않을 경우 예외를 발생시킨다.")
        void 인자_객체의_패스워드와_사용자_패스워드가_일치하지_않을_경우_예외를_발생시킨다() {
            // given
            Authentication authentication = AuthenticationFixture.createUnauthenticatedLoginIdPasswordAuthentication();

            UserDetails userDetails = mock(UserDetails.class);

            when(userDetailsService.loadUserDetailsByLoginId(authentication.getUserDetails().getLoginId())).thenReturn(userDetails);
            when(userDetails.getPassword()).thenReturn(PasswordEncoder.encode(authentication.getUserDetails().getPassword() + "2"));

            LoginIdPasswordAuthenticationProvider loginIdPasswordAuthenticationProvider = new LoginIdPasswordAuthenticationProvider(userDetailsService);

            // when & then
            try {
                loginIdPasswordAuthenticationProvider.authenticate(authentication);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(AuthenticationErrorType.AUTH_PASSWORD_MISMATCH);
            }
        }

        @Test
        @DisplayName("사용자의 상태가 활성화 상태가 아닌 경우 예외를 발생시킨다.")
        void 사용자_상태가_활성화_상태가_아닌_경우_예외를_발생시킨다() {
            // given
            Authentication authentication = AuthenticationFixture.createUnauthenticatedLoginIdPasswordAuthentication();

            UserDetails userDetails = mock(UserDetails.class);

            when(userDetailsService.loadUserDetailsByLoginId(authentication.getUserDetails().getLoginId())).thenReturn(userDetails);
            when(userDetails.getPassword()).thenReturn(PasswordEncoder.encode(authentication.getUserDetails().getPassword()));
            when(userDetails.isActive()).thenReturn(false);

            LoginIdPasswordAuthenticationProvider loginIdPasswordAuthenticationProvider = new LoginIdPasswordAuthenticationProvider(userDetailsService);

            // when & then
            try {
                loginIdPasswordAuthenticationProvider.authenticate(authentication);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(CertificationErrorType.AUTH_USER_NOT_ACTIVE);
            }
        }
    }
}