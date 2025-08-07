package com.eventty.eventtynextgen.base.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LoginRequiredAspect 클래스 테스트")
class LoginRequiredAspectTest {

    @BeforeEach
    void setUp() {
        SessionContextHolder.clearContext();
    }

    @Nested
    @DisplayName("로그인 권한 체크 테스트")
    class CheckAuthority {

        @Test
        @DisplayName("로그인이 필요하지 않다면 별다른 검증이 이루어지지 않는다.")
        void 로그인이_필요하지_않다면_별다른_검증이_이루어지지_않는다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            when(loginRequired.requireLogin()).thenReturn(false);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect();

            // when & then
            loginRequiredAspect.checkAuthority(joinPoint, loginRequired);
        }

        @Test
        @DisplayName("로그인이 필요하고 권한은 필요없을 때 사용자 정보가 존재한다면 통과한다")
        void 로그인이_필요하고_권한은_필요없을_때_사용자_정보가_존재한다면_통과한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo(1L, UserRoleType.USER.name());

            when(loginRequired.requireLogin()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect();

            // when & then
            loginRequiredAspect.checkAuthority(joinPoint, loginRequired);
        }

        @Test
        @DisplayName("로그인이 필요하고 권한은 필요없을 때 사용자 정보가 존재하지 않다면 예외가 발생한다")
        void 로그인이_필요하고_권한은_필요없을_때_사용자_정보가_존재하지_않다면_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            when(loginRequired.requireLogin()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect();

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .satisfies(ex -> {
                    assertThat(ex).isInstanceOf(CustomException.class);
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(403);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.LOGIN_REQUIRED_API);
                });
        }

        @Test
        @DisplayName("로그인 검증에 성공하고 Admin 권한 검증에 성공할 경우 통과한다")
        void 로그인_검증에_성공하고_Admin_권한_검증에_성공할_경우_통과한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo(1L, UserRoleType.ADMIN.name());

            when(loginRequired.requireLogin()).thenReturn(true);
            when(loginRequired.requireAdmin()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect();
        }

        @Test
        @DisplayName("로그인 검증에 성공하고 Host 권한 검증에 성공할 경우 통관한다")
        void 로그인_검증에_성공하고_Host_권한_검증에_성공할_경우_통과한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo(1L, UserRoleType.HOST.name());

            when(loginRequired.requireLogin()).thenReturn(true);
            when(loginRequired.requireHost()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect();
        }

        @Test
        @DisplayName("로그인 검증에 성공하고 User 권한 검증에 성공할 경우 통과한다")
        void 로그인_검증에_성공하고_User_권한_검증에_성공할_경우_통과한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo(1L, UserRoleType.USER.name());

            when(loginRequired.requireLogin()).thenReturn(true);
            when(loginRequired.requireUser()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect();
        }

        @Test
        @DisplayName("로그인 검증에 성공하지만 권한 검증에 실패할 경우 통과하지 못한다")
        void 로그인_검증에_성공하지만_권한_검증에_실패할_경우_통과하지_못한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo(1L, UserRoleType.USER.name());

            when(loginRequired.requireLogin()).thenReturn(true);
            when(loginRequired.requireAdmin()).thenReturn(true);
            when(loginRequired.requireHost()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect();

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .satisfies(ex -> {
                    assertThat(ex).isInstanceOf(CustomException.class);
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(403);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.AUTH_USER_NOT_AUTHORIZED);
                });
        }
    }
}