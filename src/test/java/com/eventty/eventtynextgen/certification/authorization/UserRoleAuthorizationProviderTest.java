package com.eventty.eventtynextgen.certification.authorization;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.fixture.AuthenticationFixture;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleAuthorizationProvider 단위 테스트")
class UserRoleAuthorizationProviderTest {

    @Mock
    private UserComponent userComponent;

    @Nested
    @DisplayName("사용자에게 권한 할당")
    class Authorize {

        @Test
        @DisplayName("사용자에게 권한 할당에 성공할 경우 승인한 토큰을 발행해준다.")
        void 사용자에게_권한_할당에_성공할_경우_승인한_토큰을_발행해준다() {
            // given
            Authentication authentication = AuthenticationFixture.createAuthenticatedLoginIdPasswordAuthentication();
            User user = mock(User.class);

            when(userComponent.findByUserId(authentication.getUserDetails().getUserId())).thenReturn(Optional.of(user));
            when(user.getUserRole()).thenReturn(UserRoleType.USER);

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider(userComponent);

            // when
            Authentication result = userRoleAuthorizationProvider.authorize(authentication);

            // then
            assertThat(result.isAuthenticated()).isTrue();
            assertThat(result.getAuthorities().isEmpty()).isFalse();
            assertThat(result.getAuthorities())
                .anyMatch(grantAuthority -> AuthorizationType.ROLE_USER.name().equals(grantAuthority.getAuthority()));
        }

        @Test
        @DisplayName("사용자를 찾지 못한 경우 예외를 발생시킨다.")
        void 사용자를_찾지_못한_경우_예외를_발생시킨다() {
            Authentication authentication = AuthenticationFixture.createAuthenticatedLoginIdPasswordAuthentication();

            when(userComponent.findByUserId(authentication.getUserDetails().getUserId())).thenReturn(Optional.empty());

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider(userComponent);

            // when & then
            try {
                userRoleAuthorizationProvider.authorize(authentication);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(UserErrorType.NOT_FOUND_USER);
            }
        }

        @Test
        @DisplayName("사용자의 역할을 찾지 못한 경우 예외를 발생시킨다")
        void 사용자의_역할을_찾지_못한_경우_예외를_발생시킨다() {
            Authentication authentication = AuthenticationFixture.createAuthenticatedLoginIdPasswordAuthentication();
            User user = mock(User.class);

            when(userComponent.findByUserId(authentication.getUserDetails().getUserId())).thenReturn(Optional.of(user));
            when(user.getUserRole()).thenReturn(null);

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider(userComponent);

            // when & then
            try {
                userRoleAuthorizationProvider.authorize(authentication);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(CertificationErrorType.AUTH_USER_ROLE_ASSIGNMENT_ERROR);
            }
        }
    }

}