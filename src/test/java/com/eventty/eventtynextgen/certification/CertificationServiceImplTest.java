package com.eventty.eventtynextgen.certification;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.JWT_TOKEN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.certification.CertificationService.CertificationLoginResult;
import com.eventty.eventtynextgen.certification.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.certification.authorization.AuthorizationProvider;
import com.eventty.eventtynextgen.certification.core.JwtTokenProvider;
import com.eventty.eventtynextgen.certification.core.RefreshTokenProvider;
import com.eventty.eventtynextgen.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CertificationServiceImpl 단위 테스트")
public class CertificationServiceImplTest {

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @DisplayName("비즈니스 로직 - 로그인")
    @Nested
    class Login {

        @Test
        @DisplayName("이메일과 비밀번호를 통해 인증에 성공할 경우 로그인에 성공하고 생성한 토큰을 반환한다.")
        void 이메일과_비밀번호로_인증에_성공할_경우_로그인에_성공하고_생성한_토큰을_반환한다() {
            // given
            String loginId = "example@gmail.com";
            String password = "password1234";

            CertificationService certificationService = new CertificationServiceImpl(authenticationProvider, jwtTokenProvider, refreshTokenProvider);

            // when
            CertificationLoginResult result = certificationService.login(loginId, password);

            // then
            assertThat(result.userId()).isNotNull();
            assertThat(result.email()).isEqualTo(loginId);
            assertThat(result.name()).isNotBlank();
            assertThat(result.tokenInfo().accessTokenType()).isEqualTo(JWT_TOKEN_TYPE);
            assertThat(result.tokenInfo().accessToken()).isNotBlank();
            assertThat(result.tokenInfo().refreshToken()).isNotBlank();
        }

        @Test
        @DisplayName("인증에 실패할 경우 로그인에 실패하고 예외가 발생한다")
        void 인증에_실패할_경우_로그인에_실패하고_예외가_발생한다() {

        }

        @Test
        @DisplayName("역할·권한 부여에 실패할 경우 로그인에 실패하고 예외가 발생한다.")
        void 역할_권한_부여에_실패할_경우_로그인에_실패하고_예외가_발생한다() {

        }
    }

}
