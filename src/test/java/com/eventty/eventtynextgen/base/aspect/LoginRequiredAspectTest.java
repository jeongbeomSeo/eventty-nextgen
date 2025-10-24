package com.eventty.eventtynextgen.base.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.base.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.context.SessionContext;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.VerifyTokenResult;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.Optional;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginRequiredAspect 클래스 테스트")
class LoginRequiredAspectTest {

    @Mock
    private UserComponent userComponent;

    @BeforeEach
    void setUp() {
        SessionContextHolder.clearContext();
    }

    @Nested
    @DisplayName("로그인 권한 체크 테스트")
    class CheckAuthority {

        @Test
        @DisplayName("로그인 검증이 필요하지 않는 API라면 검증에 통과하며 Context에 사용자 역할 정보가 업데이트된다")
        void 로그인_검증이_필요하지_않는_API라면_검증에_통과하며_Context에_사용자_역할_정보가_업데이트된다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            when(loginRequired.requireLogin()).thenReturn(false);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when  & then
            loginRequiredAspect.checkAuthority(joinPoint, loginRequired);
        }

        @Test
        @DisplayName("로그인 검증만 필요하고 사용자 조회 및 검증에 성공했다면 검증에 통과하며 Context에 사용자 역할 정보가 업데이트된다")
        void 로그인_검증만_필요하고_사용자_조회_및_검증에_성공했다면_검증에_통과하며_Context에_사용자_역할_정보가_업데이트된다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);
            Long userId = 1L;

            User user = mock(User.class);
            when(user.isDeleted()).thenReturn(false);
            when(user.getUserRole()).thenReturn(UserRoleType.USER);

            SessionContextHolder.getContext().updateSessionInfo(userId, "session_token", VerifyTokenResult.VERIFIED_TOKEN);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);
            when(userComponent.getUserByUserId(context.getUserId())).thenReturn(Optional.of(user));

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when
            loginRequiredAspect.checkAuthority(joinPoint, loginRequired);

            // then
            assertThat(SessionContextHolder.getContext().getRole()).isEqualTo(UserRoleType.USER.name());
        }

        @Test
        @DisplayName("로그인 검증과 역할 검증이 필요하고 사용자 조회 및 검증에 성공한 뒤 역할 검증에도 성공한다면 검증 통과하며 Context에 사용자 역할 정보가 업데이트된다")
        void 로그인_검증과_역할_검증이_필요하고_사용자_조회_및_검증에_성공한_뒤_역할_검증에도_성공한다면_검증_통과하며_Context에_사용자_역할_정보가_업데이트된다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);
            Long userId = 1L;

            User user = mock(User.class);
            when(user.isDeleted()).thenReturn(false);
            when(user.getUserRole()).thenReturn(UserRoleType.USER);

            SessionContextHolder.getContext().updateSessionInfo(userId, "session_token", VerifyTokenResult.VERIFIED_TOKEN);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);
            when(userComponent.getUserByUserId(context.getUserId())).thenReturn(Optional.of(user));

            when(loginRequired.requireAdmin()).thenReturn(true);
            when(loginRequired.requireHost()).thenReturn(true);
            when(loginRequired.requireUser()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when
            loginRequiredAspect.checkAuthority(joinPoint, loginRequired);

            // then
            assertThat(SessionContextHolder.getContext().getRole()).isEqualTo(UserRoleType.USER.name());
        }

        @Test
        @DisplayName("로그인 검증이 필요하지만 Session Context에 존재하는 파싱 결과가 존재하지 않다면 검증에 싪채하고 예외가 발생한다")
        void 검증이_필요하지만_Session_Context에_존재하는_파싱_결과가_존재하지_않다면_검증에_싪채하고_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo("session_token", null);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(401);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.LOGIN_REQUIRED_API);
                });
        }

        @Test
        @DisplayName("로그인 검증이 필요하지만 Session Context에 존재하는 파싱 결과가 EXPIRED_TOKEN이라면 검증에 실패하고 예외가 발생한다")
        void 로그인_검증이_필요하지만_Session_Context에_존재하는_파싱_결과가_EXPIRED_TOKEN이라면_검증에_실패하고_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo("session_token", VerifyTokenResult.EXPIRED_TOKEN);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(401);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.JWT_TOKEN_EXPIRED);
                });
        }

        @Test
        @DisplayName("로그인 검증이 필요하지만 Session Context에 존재하는 파싱 결과가 UNSUPPORTED_TOKEN이라면 검증에 실패하고 예외가 발생한다")
        void 로그인_검증이_필요하지만_Session_Context에_존재하는_파싱_결과가_UNSUPPORTED_TOKEN이라면_검증에_실패하고_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo("session_token", VerifyTokenResult.UNSUPPORTED_TOKEN);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(401);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.UNSUPPORTED_JWT_TOKEN);
                });
        }

        @Test
        @DisplayName("로그인 검증이 필요하지만 Session Context에 존재하는 파싱 결과가 ILLEGAL_STATE_TOKEN이라면 검증에 실패하고 예외가 발생한다")
        void 로그인_검증이_필요하지만_Session_Context에_존재하는_파싱_결과가_ILLEGAL_STATE_TOKEN이라면_검증에_실패하고_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo("session_token", VerifyTokenResult.ILLEGAL_STATE_TOKEN);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(401);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.ILLEGAL_STATE_JWT_TOKEN);
                });
        }

        @Test
        @DisplayName("로그인 검증이 필요하지만 Session Context에 존재하는 파싱 결과가 INVALID_SIGNATURE_TOKEN이라면 검증에 실패하고 예외가 발생한다")
        void 로그인_검증이_필요하지만_Session_Context에_존재하는_파싱_결과가_INVALID_SIGNATURE_TOKEN이라면_검증에_실패하고_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo("session_token", VerifyTokenResult.INVALID_SIGNATURE_TOKEN);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(401);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.INVALID_SIGNATURE_JWT_TOKEN);
                });
        }

        @Test
        @DisplayName("로그인 검증이 필요하지만 Session Context에 존재하는 파싱 결과가 UNKNOWN_ERROR이라면 검증에 실패하고 예외가 발생한다")
        void 로그인_검증이_필요하지만_Session_Context에_존재하는_파싱_결과가_UNKNOWN_ERROR이라면_검증에_실패하고_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);

            SessionContextHolder.getContext().updateSessionInfo("session_token", VerifyTokenResult.UNKNOWN_ERROR);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(401);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.FAILED_TOKEN_VERIFIED);
                });
        }


        @Test
        @DisplayName("로그인 검증이 필요하여 사용자 조회 결과 사용자를 찾지 못한다면 검증에 실패하고 예외가 발생한다")
        void 로그인_검증이_필요하여_사용자_조회_결과_사용자를_찾지_못한다면_검증에_실패하고_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);
            Long userId = 1L;

            SessionContextHolder.getContext().updateSessionInfo(userId, "session_token", VerifyTokenResult.VERIFIED_TOKEN);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);
            when(userComponent.getUserByUserId(context.getUserId())).thenReturn(Optional.empty());

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .isInstanceOf(CustomException.class)
                .satisfies((ex) -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(401);
                    assertThat(customException.getErrorType()).isEqualTo(UserErrorType.NOT_FOUND_USER);
                });
        }

        @Test
        @DisplayName("로그인 검증이 필요하며 사용자 조회 결과 사용자를 찾았지만 유효한 사용자가 아니라면 검증에 실패하고 예외가 발생한다")
        void 로그인_검증이_필요하며_사용자_조회_결과_사용자를_찾았지만_유효한_사용자가_아니라면_검증에_실패하고_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);
            Long userId = 1L;

            User user = mock(User.class);
            when(user.isDeleted()).thenReturn(true);

            SessionContextHolder.getContext().updateSessionInfo(userId, "session_token", VerifyTokenResult.VERIFIED_TOKEN);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);
            when(userComponent.getUserByUserId(context.getUserId())).thenReturn(Optional.of(user));

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .isInstanceOf(CustomException.class)
                .satisfies((ex) -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(401);
                    assertThat(customException.getErrorType()).isEqualTo(UserErrorType.USER_ALREADY_DELETED);
                });
        }

        @Test
        @DisplayName("로그인 검증과 역할 검증이 필요하고 사용자의 모든 검증에 성공했지만 역할 검증에 실패핟다면 검증은 실패하고 예외가 발생한다")
        void 로그인_검증과_역할_검증이_필요하고_사용자의_모든_검증에_성공했지만_역할_검증에_실패핟다면_검증은_실패하고_예외가_발생한다() {
            // given
            JoinPoint joinPoint = mock(JoinPoint.class);
            LoginRequired loginRequired = mock(LoginRequired.class);
            Long userId = 1L;

            User user = mock(User.class);
            when(user.isDeleted()).thenReturn(false);
            when(user.getUserRole()).thenReturn(UserRoleType.USER);

            SessionContextHolder.getContext().updateSessionInfo(userId, "session_token", VerifyTokenResult.VERIFIED_TOKEN);
            SessionContext context = SessionContextHolder.getContext();

            when(loginRequired.requireLogin()).thenReturn(true);
            when(userComponent.getUserByUserId(context.getUserId())).thenReturn(Optional.of(user));

            when(loginRequired.requireAdmin()).thenReturn(true);
            when(loginRequired.requireHost()).thenReturn(true);
            when(loginRequired.requireUser()).thenReturn(false);

            LoginRequiredAspect loginRequiredAspect = new LoginRequiredAspect(userComponent);

            // when & then
            assertThatThrownBy(() -> loginRequiredAspect.checkAuthority(joinPoint, loginRequired))
                .isInstanceOf(CustomException.class)
                .satisfies((ex) -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(403);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.AUTH_USER_NOT_AUTHORIZED);
                });
        }
    }
}