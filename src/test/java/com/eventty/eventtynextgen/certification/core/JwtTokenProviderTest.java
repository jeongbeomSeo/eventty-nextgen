package com.eventty.eventtynextgen.certification.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.certification.constant.CertificationConst;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.TokenInfo;
import com.eventty.eventtynextgen.certification.fixture.AuthenticationFixture;
import com.eventty.eventtynextgen.certification.refreshtoken.RefreshTokenService;
import com.eventty.eventtynextgen.certification.refreshtoken.entity.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

    @Nested
    @DisplayName("토큰 생성")
    class CreateToken {

        // Test Key (테스트용 키)
        private static final String SECRET_KEY = "ee77845fbe673b5153a497f7ec9ad4b2c11556c3c359dadfc481728850c35bc39924819876ed0cd87742ba85901e5efcf91dc9fa2dba24eaf9a1da06f768586a";
        private static final Long TOKEN_VALIDITY_IN_MIN = 10L;

        @Test
        @DisplayName("인증된 사용자는 토큰 생성에 성공한다.")
        void 인증된_사용자는_토큰_생성에_성공한다() {
            // given
            Authentication authentication = AuthenticationFixture.createAuthorizedLoginIdPasswordAuthentication(1L, "email@gmail.com", "plain_password");

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET_KEY, TOKEN_VALIDITY_IN_MIN, TOKEN_VALIDITY_IN_MIN);

            // when
            TokenInfo token = jwtTokenProvider.createToken(authentication);

            // then
            assertThat(token.getTokenType()).isEqualTo(CertificationConst.JWT_TOKEN_TYPE);
            assertThat(token.getAccessToken()).isNotBlank();
            assertThat(token.getRefreshToken()).isNotBlank();
        }

        @Test
        @DisplayName("인증되지 않은 사용자는 토큰 생성에 실패하고 예외를 발생시킨다.")
        void 인증되지_않은_사용자는_토큰_생성에_실패하고_예외를_발생시킨다() {
            // given
            Authentication authentication = AuthenticationFixture.createUnauthenticatedLoginIdPasswordAuthentication();

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET_KEY, TOKEN_VALIDITY_IN_MIN, TOKEN_VALIDITY_IN_MIN);

            // when & then
            try {
                jwtTokenProvider.createToken(authentication);
            } catch (Exception ex) {
                assertThat(ex.getClass()).isEqualTo(IllegalArgumentException.class);
                assertThat(ex.getMessage()).isEqualTo("Only authorized users can generate a JWT token.");
            }
        }
    }

}