package com.eventty.eventtynextgen.certification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.TokenInfo;
import com.eventty.eventtynextgen.certification.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.certification.authorization.AuthorizationProvider;
import com.eventty.eventtynextgen.certification.constant.CertificationConst;
import com.eventty.eventtynextgen.certification.refreshtoken.RefreshTokenService;
import com.eventty.eventtynextgen.certification.refreshtoken.entity.RefreshToken;
import com.eventty.eventtynextgen.certification.response.CertificationReissueResponseView;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Certification Service 단위 테스트")
class CertificationServiceImplTest {

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private AuthorizationProvider authorizationProvider;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserComponent userComponent;

    @Nested
    @DisplayName("비즈니스 로직 - 토큰 재발급")
    class Reissue {

        @Test
        @DisplayName("저장되어 있는 유효한 RefreshToken을 이용한 토큰 재발급 요청은 `성공`한다")
        void 저장되어_있는_유효한_리프래시_토큰을_이용한_재발급_요청은_성공한다() {
            // given
            Long userId = 1L;
            String expiredAccessToken = "expired_access_token";
            String refreshToken = "valid_refresh_token";
            HttpServletResponse response = mock(HttpServletResponse.class);
            User user = mock(User.class);

            RefreshToken refreshTokenFromDb = mock(RefreshToken.class);
            TokenInfo tokenInfo = createMockTokenInfo();

            doNothing().when(jwtTokenProvider).verifyToken(refreshToken);
            when(refreshTokenService.getRefreshToken(userId)).thenReturn(refreshTokenFromDb);
            when(refreshTokenFromDb.getRefreshToken()).thenReturn(refreshToken);
            when(userComponent.findByUserId(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(false);
            when(jwtTokenProvider.createTokenByExpiredToken(expiredAccessToken)).thenReturn(tokenInfo);
            when(refreshTokenService.saveOrUpdate(tokenInfo.getRefreshToken(), userId)).thenReturn(mock(RefreshToken.class));

            CertificationServiceImpl certificationService = new CertificationServiceImpl(authenticationProvider, authorizationProvider, jwtTokenProvider,
                refreshTokenService, userComponent);

            // when
            CertificationReissueResponseView result = certificationService.reissue(userId, expiredAccessToken, refreshToken, response);

            // then
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.accessTokenInfo().accessToken()).isEqualTo(tokenInfo.getAccessToken());
            assertThat(result.accessTokenInfo().tokenType()).isEqualTo(CertificationConst.JWT_TOKEN_TYPE);
        }

        @Test
        @DisplayName("만료기간이 지난 RefreshToken을 이용한 토큰 재발급 요청은 `실패`한다")
        void 만료기간이_지난_리프래시_토큰을_이용한_토큰_재발급_요청은_실패한다() {
            // given
            Long userId = 1L;
            String expiredAccessToken = "expired_access_token";
            String expiredRefreshToken = "expired_refresh_token";
            HttpServletResponse response = mock(HttpServletResponse.class);

            doThrow(ExpiredJwtException.class).when(jwtTokenProvider).verifyToken(expiredRefreshToken);

            CertificationServiceImpl certificationService = new CertificationServiceImpl(authenticationProvider, authorizationProvider, jwtTokenProvider,
                refreshTokenService, userComponent);

            // when & then
            assertThatThrownBy(() ->
                certificationService.reissue(userId, expiredAccessToken, expiredRefreshToken, response))
                .extracting(ex -> ((CustomException) ex).getErrorType()).isEqualTo(CertificationErrorType.JWT_TOKEN_EXPIRED);
        }

        @Test
        @DisplayName("형식이 잘못된 RefreshToken을 이용한 토큰 재발급 요청은 `실패`한다")
        void 형식이_잘못된_리프래시_토큰을_이용한_토큰_재발급_요청은_실패한다() {
            // given
            Long userId = 1L;
            String expiredAccessToken = "expired_access_token";
            String expiredRefreshToken = "expired_refresh_token";
            HttpServletResponse response = mock(HttpServletResponse.class);

            doThrow(IllegalStateException.class).when(jwtTokenProvider).verifyToken(expiredRefreshToken);

            CertificationServiceImpl certificationService = new CertificationServiceImpl(authenticationProvider, authorizationProvider, jwtTokenProvider,
                refreshTokenService, userComponent);

            // when & then
            assertThatThrownBy(() ->
                certificationService.reissue(userId, expiredAccessToken, expiredRefreshToken, response))
                .extracting(ex -> ((CustomException) ex).getErrorType()).isEqualTo(CertificationErrorType.FAILED_TOKEN_VERIFIED);
        }

        @Test
        @DisplayName("서명이 잘못된 RefreshToken을 이용한 토큰 재발급 요청은 `실패`한다")
        void 서멍이_잘못된_리프래시_토큰을_이용한_토큰_재발급_요청은_실패한다() {
            // given
            Long userId = 1L;
            String expiredAccessToken = "expired_access_token";
            String expiredRefreshToken = "expired_refresh_token";
            HttpServletResponse response = mock(HttpServletResponse.class);

            doThrow(SignatureException.class).when(jwtTokenProvider).verifyToken(expiredRefreshToken);

            CertificationServiceImpl certificationService = new CertificationServiceImpl(authenticationProvider, authorizationProvider, jwtTokenProvider,
                refreshTokenService, userComponent);

            // when & then
            assertThatThrownBy(() ->
                certificationService.reissue(userId, expiredAccessToken, expiredRefreshToken, response))
                .extracting(ex -> ((CustomException) ex).getErrorType()).isEqualTo(CertificationErrorType.FAILED_TOKEN_VERIFIED);
        }

        @Test
        @DisplayName("저장되어 있지 않은 RefreshToken을 이용한 토큰 재발급 요청은 `실패`한다")
        void 저장되어_있지_않은_리프래시_토큰을_이용한_토큰_재발급_요청은_실패한다() {
            // given
            Long userId = 1L;
            String expiredAccessToken = "expired_access_token";
            String refreshToken = "refresh_token";
            HttpServletResponse response = mock(HttpServletResponse.class);

            doNothing().when(jwtTokenProvider).verifyToken(refreshToken);
            doThrow(CustomException.class).when(refreshTokenService).getRefreshToken(userId);

            CertificationServiceImpl certificationService = new CertificationServiceImpl(authenticationProvider, authorizationProvider, jwtTokenProvider,
                refreshTokenService, userComponent);

            // when & then
            assertThatThrownBy(() ->
                certificationService.reissue(userId, expiredAccessToken, refreshToken, response))
                .isInstanceOf(CustomException.class);
        }

        @Test
        @DisplayName("사용자 id를 통해 조회한 RefreshToken과 값이 일치하지 않을 경우 토큰 재발급 요청은 `실패`한다")
        void 사용자_id를_통해_조회한_리프래시_토큰과_값이_일치하지_않을_경우_토큰_재발급_요청은_실패한다() {
            // given
            Long userId = 1L;
            String expiredAccessToken = "expired_access_token";
            String refreshToken = "refresh_token";
            HttpServletResponse response = mock(HttpServletResponse.class);

            RefreshToken refreshTokenFromDb = mock(RefreshToken.class);

            doNothing().when(jwtTokenProvider).verifyToken(refreshToken);
            when(refreshTokenService.getRefreshToken(userId)).thenReturn(refreshTokenFromDb);
            when(refreshTokenFromDb.getRefreshToken()).thenReturn("mismatch_refresh_token");

            CertificationServiceImpl certificationService = new CertificationServiceImpl(authenticationProvider, authorizationProvider, jwtTokenProvider,
                refreshTokenService, userComponent);

            // when & then
            assertThatThrownBy(() ->
                certificationService.reissue(userId, expiredAccessToken, refreshToken, response))
                .extracting(ex -> ((CustomException) ex).getErrorType()).isEqualTo(CertificationErrorType.MISMATCH_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("사용자가 삭제된 상태로 변경되었을 경우 토큰 재발급 요청은 `실패`한다.")
        void 사용자가_삭제된_상태로_변경되었을_경우_토큰_재발급_요청은_실패한다() {
            // given
            Long userId = 1L;
            String expiredAccessToken = "expired_access_token";
            String refreshToken = "refresh_token";
            HttpServletResponse response = mock(HttpServletResponse.class);
            User user = mock(User.class);

            RefreshToken refreshTokenFromDb = mock(RefreshToken.class);

            doNothing().when(jwtTokenProvider).verifyToken(refreshToken);
            when(refreshTokenService.getRefreshToken(userId)).thenReturn(refreshTokenFromDb);
            when(refreshTokenFromDb.getRefreshToken()).thenReturn(refreshToken);
            when(userComponent.findByUserId(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(true);

            CertificationServiceImpl certificationService = new CertificationServiceImpl(authenticationProvider, authorizationProvider, jwtTokenProvider,
                refreshTokenService, userComponent);

            // when & then
            assertThatThrownBy(() ->
                certificationService.reissue(userId, expiredAccessToken, refreshToken, response))
                .extracting(ex -> ((CustomException) ex).getErrorType()).isEqualTo(UserErrorType.USER_ALREADY_DELETED);
        }

        private TokenInfo createMockTokenInfo() {
            TokenInfo mock = mock(TokenInfo.class);
            when(mock.getTokenType()).thenReturn(CertificationConst.JWT_TOKEN_TYPE);
            when(mock.getAccessToken()).thenReturn("new_access_token");
            when(mock.getRefreshToken()).thenReturn("new_refresh_token");
            return mock;
        }
    }
}