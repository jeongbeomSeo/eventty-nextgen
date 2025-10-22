package com.eventty.eventtynextgen.base.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.AccessTokenPayload;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.CertificationTokenPayload;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.SessionTokenInfo;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.VerifyTokenResult;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Jwt Token Provider Utility Class 단위 테스트")
class JwtTokenProviderTest {

    @Nested
    @DisplayName("Session Token 생성 테스트")
    class CreateSessionToken {

        @Test
        @DisplayName("사용자 ID와 액세스 토큰과 리프래시 토큰에 대한 만료 정보를 인자로 받아 Session Token을 생성한다")
        void 사용자_ID와_액세스_토큰과_리프래시_토큰에_대한_만료_정보를_인자로_받아_Session_Token을_생성한다() {
            // given
            Long userId = 1L;
            Long accessTokenExpiresIn = 1000L;
            Long refreshTokenExpiresIn = 10000L;

            // when
            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, accessTokenExpiresIn, refreshTokenExpiresIn);

            // then
            assertThat(sessionToken.getAccessToken()).isNotBlank();
            assertThat(sessionToken.getRefreshToken()).isNotBlank();
        }

        @Test
        @DisplayName("사용자 ID가 null일 경우 토큰 생성에 실패하고 예외가 발생한다")
        void 사용자_ID가_null일_경우_토큰_생성에_실패하고_예외가_발생한다() {
            // given
            Long userId = null;
            Long accessTokenExpiresIn = 1000L;
            Long refreshTokenExpiresIn = 10000L;

            // when & then
            assertThatThrownBy(() -> JwtTokenProvider.createSessionToken(userId, accessTokenExpiresIn, refreshTokenExpiresIn))
                .isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    @DisplayName("Access Token의 페이로드를 추출 테스트")
    class ExtractAccessTokenPayloadIgnoringExpiration {

        @Test
        @DisplayName("인자로 들어온 Access Token을 파싱하는데 성공하면 페이로드를 받는다")
        void 인자로_들어온_Access_Token을_파싱하는데_성공하면_페이로드를_받는다() {
            // given
            Long userId = 1L;
            Long accessTokenExpiresIn = 1000L;
            Long refreshTokenExpiresIn = 10000L;
            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, accessTokenExpiresIn, refreshTokenExpiresIn);

            // when
            AccessTokenPayload accessTokenPayload = JwtTokenProvider.extractAccessTokenPayloadIgnoringExpiredExpiration(sessionToken.getAccessToken());

            // then
            assertThat(accessTokenPayload.getUserId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("인자로 들어온 Access Token이 만료되었어도 페이로드를 받는다")
        void 인자로_들어온_Access_Token이_만료되었다면_예외가_발생한다() {
            // given
            Long userId = 1L;
            Long accessTokenExpiresIn = -1000L;
            Long refreshTokenExpiresIn = 10000L;
            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, accessTokenExpiresIn, refreshTokenExpiresIn);

            // when
            AccessTokenPayload accessTokenPayload = JwtTokenProvider.extractAccessTokenPayloadIgnoringExpiredExpiration(sessionToken.getAccessToken());

            // then
            assertThat(accessTokenPayload.getUserId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("만료된 토큰을 제외한 유효하지 않은 토큰에 대한 파싱은 400 예외가 발생한다")
        void 만료된_토큰을_제외한_유효하지_않은_토큰에_대한_파싱은_400_예외가_발생한다() {
            // given
            String accessToken = "invalid_access_token";

            // when & then
            assertThatThrownBy(() -> JwtTokenProvider.extractAccessTokenPayloadIgnoringExpiredExpiration(accessToken))
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(500);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.FAIL_PARSING_JWT_TOKEN);
                });
        }
    }


    @Nested
    @DisplayName("토큰 검증 테스트")
    class VerifyToken {

        @Test
        @DisplayName("토큰 검증에 성공할 경우 VerifyTokenResult.VERIFIED_TOKEN을 반환한다")
        void 토큰_검증에_성공할_경우_VERIFIED_TOKEN을_반환한다() {
            // given
            Long userId = 1L;
            Long accessTokenExpiresIn = 1000L;
            Long refreshTokenExpiresIn = 10000L;
            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, accessTokenExpiresIn, refreshTokenExpiresIn);

            // when
            VerifyTokenResult verifyTokenResult = JwtTokenProvider.verifyToken(sessionToken.getAccessToken());

            // then
            assertThat(verifyTokenResult).isEqualTo(VerifyTokenResult.VERIFIED_TOKEN);
        }

        @Test
        @DisplayName("토큰이 만료되었을 경우 VerifyTokenResult.EXPIRED_TOKEN을 반환한다")
        void 토큰이_만료되었을_경우_EXPIRED_TOKEN을_반환한다() {
            // given
            Long userId = 1L;
            Long accessTokenExpiresIn = -1000L;
            Long refreshTokenExpiresIn = 10000L;
            SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, accessTokenExpiresIn, refreshTokenExpiresIn);

            // when
            VerifyTokenResult verifyTokenResult = JwtTokenProvider.verifyToken(sessionToken.getAccessToken());

            // then
            assertThat(verifyTokenResult).isEqualTo(VerifyTokenResult.EXPIRED_TOKEN);
        }

        @Test
        @DisplayName("토큰이 잘못된 형식으로 되어있을 경우 VerifyTokenResult.ILLGAL_STATE_TOKEN을 반환한다")
        void 토큰이_잘못된_형식으로_되어있을_경우_ILLEGAL_STATE_TOKEN을_반환한다() {
            // given
            String accessToken = "invalid_access_token";

            // when
            VerifyTokenResult verifyTokenResult = JwtTokenProvider.verifyToken(accessToken);

            // then
            assertThat(verifyTokenResult).isEqualTo(VerifyTokenResult.ILLEGAL_STATE_TOKEN);
        }

        @Test
        @DisplayName("토큰 값으로 null이 들어올 경우 VerifyTokenResult.UNKNOWN_ERROR를 반환한다")
        void 토큰_값으로_null이_들어올_경우_UNKNOWN_ERROR를_반환한다() {
            // given
            String accessToken = null;

            // when
            VerifyTokenResult verifyTokenResult = JwtTokenProvider.verifyToken(accessToken);

            // then
            assertThat(verifyTokenResult).isEqualTo(VerifyTokenResult.UNKNOWN_ERROR);
        }
    }

    @Nested
    @DisplayName("Certification Token 생성 테스트")
    class CreateCertificationToken {

        @Test
        @DisplayName("앱 네임, 호출 권환과 만료 정보를 받아 Certification Token을 생성한다")
        void 앱_네임_호출_권한_만료_정보를_받아_Certification_Token을_생성한다() {
            // given
            String appName = "client";
            Set<String> apiPermissions = Set.of("user");
            long certificationTokenValidityInMS = 10000L;

            // when
            CertificationTokenInfo certificationToken = JwtTokenProvider.createCertificationToken(appName, apiPermissions, certificationTokenValidityInMS);

            // then
            assertThat(certificationToken.getTokenType()).isEqualTo("Bearer");
            assertThat(certificationToken.getCertificationToken()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Certification Token의 페이로드를 추출 테스트")
    class ExtractCertificationTokenPayloadIgnoringExpiration {

        @Test
        @DisplayName("인자로 들어온 certification token을 파싱하는데 성공하면 페이로드를 받는다")
        void 인자로_들어온_certification_token을_파싱하는데_성공하면_페이로드를_받는다() {
            // given
            String appName = "client";
            Set<String> apiPermissions = Set.of("user");
            long certificationTokenValidityInMS = 10000L;
            CertificationTokenInfo certificationToken = JwtTokenProvider.createCertificationToken(appName, apiPermissions, certificationTokenValidityInMS);

            // when
            CertificationTokenPayload certificationTokenPayload = JwtTokenProvider.extractCertificationTokenPayloadIgnoringExpiredExpiration(
                certificationToken.getCertificationToken());

            // then
            assertThat(certificationTokenPayload.getAppName()).isEqualTo(appName);
            assertThat(certificationTokenPayload.getApiPermission()).isEqualTo(apiPermissions);
            assertThat(certificationTokenPayload.getAdminEmail()).isNotBlank();
        }

        @Test
        @DisplayName("인자로 들어올 certification token이 만료되었어도 페이로드를 받는다")
        void 인자로_들어온_certification_token이_만료되었어도_페이로드를_받는다() {
            // given
            String appName = "client";
            Set<String> apiPermissions = Set.of("user");
            long certificationTokenValidityInMS = -10000L;
            CertificationTokenInfo certificationToken = JwtTokenProvider.createCertificationToken(appName, apiPermissions, certificationTokenValidityInMS);

            // when
            CertificationTokenPayload certificationTokenPayload = JwtTokenProvider.extractCertificationTokenPayloadIgnoringExpiredExpiration(
                certificationToken.getCertificationToken());

            // then
            assertThat(certificationTokenPayload.getAppName()).isEqualTo(appName);
            assertThat(certificationTokenPayload.getApiPermission()).isEqualTo(apiPermissions);
            assertThat(certificationTokenPayload.getAdminEmail()).isNotBlank();
        }

        @Test
        @DisplayName("만료된 토큰을 제외한 유효하지 않은 토큰에 대한 파싱은 500 예외가 발생한다")
        void 만료된_토큰을_제외한_유효하지_않은_토큰에_대한_파싱은_500_예외가_발생한다() {
            // given
            String certificationToken = "invalid_certification_token";

            // when & then
            assertThatThrownBy(() -> JwtTokenProvider.extractCertificationTokenPayloadIgnoringExpiredExpiration(certificationToken))
                .satisfies(ex -> {
                    CustomException customException = (CustomException) ex;
                    assertThat(customException.getHttpStatus().value()).isEqualTo(500);
                    assertThat(customException.getErrorType()).isEqualTo(AuthErrorType.FAIL_PARSING_JWT_TOKEN);
                });
        }
    }

}