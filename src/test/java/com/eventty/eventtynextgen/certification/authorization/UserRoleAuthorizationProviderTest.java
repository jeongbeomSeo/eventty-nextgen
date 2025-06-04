package com.eventty.eventtynextgen.certification.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.GrantedAuthority;
import com.eventty.eventtynextgen.certification.fixture.AuthenticationFixture;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.List;
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
        @DisplayName("인증되지 않은 사용자는 권한 할당을 받지 못하고 예외가 발생한다")
        void 인증되지_않은_사용자는_권한_할당을_받지_못하고_예외가_발생한다() {
            Authentication authentication = AuthenticationFixture.createUnauthenticatedLoginIdPasswordAuthentication();

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider();

            // when & then
            assertThatThrownBy(() ->
                userRoleAuthorizationProvider.authorize(authentication))
                .extracting(ex -> ((CustomException) ex).getErrorType()).isEqualTo(CertificationErrorType.NOT_ALLOWED_AUTHORIZE_WITHOUT_AUTHENTICATION);
        }
    }

    @Nested
    @DisplayName("사용자 역할을 권한으로 변경하여 가져오기")
    class GetAuthoritiesByUserRole {

        @Test
        @DisplayName("사용자 역할이 HOST인 경우 올바르게 권한 할당이 이루어진다.")
        void 사용자_역할이_HOST인_경우_올바르게_권한_할당이_이루어진다() {
            // given
            UserRoleType userRoleType = UserRoleType.HOST;

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider();

            // when
            List<GrantedAuthority> authorities = userRoleAuthorizationProvider.getAuthoritiesByUserRole(userRoleType);

            // then
            assertThat(authorities.get(0).getAuthority()).isEqualTo("ROLE_HOST");
        }

        @Test
        @DisplayName("사용자 역할이 ADMIN인 경우 올바르게 권한 할당이 이루어진다.")
        void 사용자_역할이_ADMIN인_경우_올바르게_권한_할당이_이루어진다() {
            // given
            UserRoleType userRoleType = UserRoleType.ADMIN;

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider();

            // when
            List<GrantedAuthority> authorities = userRoleAuthorizationProvider.getAuthoritiesByUserRole(userRoleType);

            // then
            assertThat(authorities.get(0).getAuthority()).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("사용자 역할이 USER인 경우 올바르게 권한 할당이 이루어진다.")
        void 사용자_역할이_USER인_경우_올바르게_권한_할당이_이루어진다() {
            // given
            UserRoleType userRoleType = UserRoleType.USER;

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider();

            // when
            List<GrantedAuthority> authorities = userRoleAuthorizationProvider.getAuthoritiesByUserRole(userRoleType);

            // then
            assertThat(authorities.get(0).getAuthority()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("사용자 역할이 null인 경우 권한 할당에 실패한다")
        void 사용자_역할이_null인_경우_권한_할당에_실패한다() {
            // given
            UserRoleType userRoleType = null;

            UserRoleAuthorizationProvider userRoleAuthorizationProvider = new UserRoleAuthorizationProvider();

            // when & then
            assertThatThrownBy(() ->
                userRoleAuthorizationProvider.getAuthoritiesByUserRole(userRoleType))
                .extracting(ex -> ((CustomException) ex).getErrorType()).isEqualTo(CertificationErrorType.AUTH_USER_ROLE_ASSIGNMENT_ERROR);
        }
    }

}