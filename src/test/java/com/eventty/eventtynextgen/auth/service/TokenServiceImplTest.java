package com.eventty.eventtynextgen.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.SessionTokenInfo;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.VerifyTokenResult;
import com.eventty.eventtynextgen.auth.core.Authentication;
import com.eventty.eventtynextgen.auth.core.userdetails.UserDetails;
import com.eventty.eventtynextgen.auth.refreshtoken.RefreshTokenService;
import com.eventty.eventtynextgen.auth.refreshtoken.entity.RefreshToken;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CommonErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.JwtTokenErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

// TODO: JwtTokenProvider를 Utility Class로 수정하였으니 이에 맞춰서 테스트 코드도 수정.
@ExtendWith(MockitoExtension.class)
@DisplayName("TokenServiceImpl 단위 테스트")
class TokenServiceImplTest {
    @Mock
    private RefreshTokenService refreshTokenService;

    @Nested
    @DisplayName("토큰 발행 및 리프래시 토큰 저장 테스트")
    class IssueTokensAndSaveRefreshToken {

        @Test
        @DisplayName("권한을 인가 받은 Authentcation을 인자로 받을 경우 토큰 생성 및 리프래시 토큰 저장에 성공한다")
        void 권한을_인가_받은_Authentication을_인자로_받을_경우_토큰_생성_및_리프래시_토큰_저장에_성공한다() {
            // given
            Authentication authorizedAuthentication = mock(Authentication.class);
            SessionTokenInfo loginTokensInfo = mock(SessionTokenInfo.class);
            UserDetails userDetails = mock(UserDetails.class);
            RefreshToken refreshToken = mock(RefreshToken.class);

            when(authorizedAuthentication.isAuthorized()).thenReturn(true);
            when(loginTokensInfo.getRefreshToken()).thenReturn("refresh_token");
            when(authorizedAuthentication.getUserDetails()).thenReturn(userDetails);
            when(userDetails.getUserId()).thenReturn(1L);
            when(refreshTokenService.saveOrUpdate(loginTokensInfo.getRefreshToken(), authorizedAuthentication.getUserDetails().getUserId()))
                .thenReturn(refreshToken);

            TokenServiceImpl tokenService = new TokenServiceImpl(refreshTokenService);

            // when
            SessionTokenInfo result = tokenService.issueTokenAndSaveRefresh(authorizedAuthentication);

            // then
            assertThat(result).isEqualTo(loginTokensInfo);
        }

        @Test
        @DisplayName("권한을 인가 받지 못한 Authentication을 인자로 받을 경우 생성 및 저장에 실패한다")
        void 권한을_인가_받지_못한_Authentcation을_인자로_받을_경우_생성_및_저장에_실패한다() {
            // given
            Authentication notAuthorizedAuthentication = mock(Authentication.class);

            when(notAuthorizedAuthentication.isAuthorized()).thenReturn(false);

            TokenServiceImpl tokenService = new TokenServiceImpl(refreshTokenService);

            // when & then
            assertThatThrownBy(() ->
                tokenService.issueTokenAndSaveRefresh(notAuthorizedAuthentication))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getErrorType()).isEqualTo(CommonErrorType.INVALID_INPUT_DATA);
                    assertThat(customException.getDetail()).isEqualTo("Login Token의 발행은 인증·인가에 성공한 Authentcation 객체만 호출할 수 있습니다.");
                });
        }
    }

    @Nested
    @DisplayName("리프래시 토큰 검증 및 매칭 테스트")
    class VerifyAndMatchRefresh {

        @Test
        @DisplayName("토큰 검증에 성공하고 DB를 통해 가져온 리프래시 토큰값과 일치할 경우 로직에 성공한다")
        void 토큰_검증에_성공하고_DB를_통해_가져온_리프래시_토큰값과_일치할_경우_로직에_성공한다() {
            // given
            String refreshToken = "refresh_token";
            Long userId = 1L;
            RefreshToken refreshTokenFromDb = mock(RefreshToken.class);

            when(refreshTokenService.getRefreshToken(userId)).thenReturn(refreshTokenFromDb);
            when(refreshTokenFromDb.getRefreshToken()).thenReturn("refresh_token");

            TokenServiceImpl tokenService = new TokenServiceImpl(refreshTokenService);

            // when & then
            tokenService.verifyAndMatchRefresh(refreshToken, userId);
        }

        @Test
        @DisplayName("토큰 검증에 성공하고 DB를 통해 가져온 리프래시 토큰값과 일치하지 않을 경우 예외를 발생시킨다")
        void 토큰_검증에_성공하고_DB를_통해_가져온_리프래시_토큰값과_일치하지_않을_경우_예외를_발생시킨다() {
            // given
            String refreshToken = "refresh_token";
            Long userId = 1L;

            doThrow(CustomException.class).when(refreshTokenService).getRefreshToken(userId);

            TokenServiceImpl tokenService = new TokenServiceImpl(refreshTokenService);

            // when & then
            assertThatThrownBy(() ->
                tokenService.verifyAndMatchRefresh(refreshToken, userId))
                .isInstanceOf(CustomException.class);
        }

        @Test
        @DisplayName("만료된 토큰이라면 토큰 검증에 실패하고 예외를 발생시킨다")
        void 만료된_토큰이라면_토큰_검증에_실패하고_예외를_발생시킨다() {
            // given
            String expiredRefreshToken = "refresh_token";
            Long userId = 1L;

            TokenServiceImpl tokenService = new TokenServiceImpl(refreshTokenService);

            // when & then
            assertThatThrownBy(() ->
                tokenService.verifyAndMatchRefresh(expiredRefreshToken, userId))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus()).isEqualTo(HttpStatus.valueOf(400));
                    assertThat(customException.getErrorType()).isEqualTo(JwtTokenErrorType.EXPIRED_TOKEN);
                });
        }

        @Test
        @DisplayName("지원하지 않는 형태의 토큰이라면 토큰 검증에 실패하고 예외를 발생시킨다")
        void 지원하지_않는_형태의_토큰이라면_토큰_검증에_실패하고_예외를_발생시킨다() {
            // given
            String expiredRefreshToken = "refresh_token";
            Long userId = 1L;

            TokenServiceImpl tokenService = new TokenServiceImpl(refreshTokenService);

            // when & then
            assertThatThrownBy(() ->
                tokenService.verifyAndMatchRefresh(expiredRefreshToken, userId))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus()).isEqualTo(HttpStatus.valueOf(400));
                    assertThat(customException.getErrorType()).isEqualTo(JwtTokenErrorType.UNSUPPORTED_TOKEN);
                });
        }

        @Test
        @DisplayName("올바르지 않은 포맷의 토큰이라면 토큰 검증에 실패하고 예외를 발생시킨다")
        void 올바르지_않은_포맷의_토큰이라면_토큰_검증에_실패하고_예외를_발생시킨다() {
            // given
            String expiredRefreshToken = "refresh_token";
            Long userId = 1L;


            TokenServiceImpl tokenService = new TokenServiceImpl(refreshTokenService);

            // when & then
            assertThatThrownBy(() ->
                tokenService.verifyAndMatchRefresh(expiredRefreshToken, userId))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus()).isEqualTo(HttpStatus.valueOf(400));
                    assertThat(customException.getErrorType()).isEqualTo(JwtTokenErrorType.ILLEGAL_STATE_TOKEN);
                });
        }

        @Test
        @DisplayName("토큰의 서명 검증에 실패한다면 예외를 발생시킨다")
        void 토큰의_서명_검증에_실패한다면_예외를_발생시킨다() {
            // given
            String expiredRefreshToken = "refresh_token";
            Long userId = 1L;

            TokenServiceImpl tokenService = new TokenServiceImpl(refreshTokenService);

            // when & then
            assertThatThrownBy(() ->
                tokenService.verifyAndMatchRefresh(expiredRefreshToken, userId))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus()).isEqualTo(HttpStatus.valueOf(400));
                    assertThat(customException.getErrorType()).isEqualTo(JwtTokenErrorType.FAILED_SIGNATURE_VALIDATION);
                });
        }

        @Test
        @DisplayName("토큰 검증 과정에서 알 수 없는 예외가 발생한 경우 예외를 발생시킨다")
        void 토큰_검증_과정에서_알_수_없는_예외가_발생한_경우_예외를_발생시킨다() {
            // given
            String expiredRefreshToken = "refresh_token";
            Long userId = 1L;

            TokenServiceImpl tokenService = new TokenServiceImpl(refreshTokenService);

            // when & then
            assertThatThrownBy(() ->
                tokenService.verifyAndMatchRefresh(expiredRefreshToken, userId))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus()).isEqualTo(HttpStatus.valueOf(500));
                    assertThat(customException.getErrorType()).isEqualTo(JwtTokenErrorType.UNKNOWN_VERIFY_ERROR);
                });
        }
    }

}