package com.eventty.eventtynextgen.certification.authorization;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.fixture.AuthenticationFixture;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleAuthorizationProvider 단위 테스트")
class UserRoleAuthorizationProviderTest {

    @Nested
    @DisplayName("사용자에게 권한 할당")
    class Authorize {

        @Test
        @DisplayName("사용자에게 권한 할당에 성공할 경우 승인한 토큰을 발행해준다.")
        void 사용자에게_권한_할당에_성공할_경우_승인한_토큰을_발행해준다() {
            // given
            Authentication authentication = AuthenticationFixture.createAuthenticatedLoginIdPasswordAuthentication();

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider();

            // when
            Authentication result = userRoleAuthorizationProvider.authorize(authentication);

            // then
            assertThat(result.isAuthenticated()).isTrue();
            assertThat(result.getAuthorities().isEmpty()).isFalse();
            assertThat(result.getAuthorities())
                .anyMatch(grantAuthority -> AuthorizationType.ROLE_USER.name().equals(grantAuthority.getAuthority()));
        }

        @Test
        @DisplayName("사용자에게 권한 할당이 불가능한 경우 예외를 발생시킨다")
        void 사용자의_역할을_찾지_못한_경우_예외를_발생시킨다() {
            Authentication authentication = AuthenticationFixture.createUnauthenticatedLoginIdPasswordAuthentication();

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider();

            // when & then
            try {
                userRoleAuthorizationProvider.authorize(authentication);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(CertificationErrorType.AUTH_USER_ROLE_ASSIGNMENT_ERROR);
            }
        }
    }

}