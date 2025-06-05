package com.eventty.eventtynextgen.auth.authcode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.auth.authcode.entity.AuthCode;
import com.eventty.eventtynextgen.auth.authcode.repository.AuthCodeRepository;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeSendCodeResponseView;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeValidateCodeResponseView;
import com.eventty.eventtynextgen.component.EmailSenderServiceImpl;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.AuthErrorType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthCodeServiceImpl 단위 테스트")
class AuthCodeServiceImplTest {

    @Mock
    private UserComponent userComponent;

    @Mock
    private AuthCodeRepository authCodeRepository;

    @Mock
    private EmailSenderServiceImpl emailSenderService;

    @DisplayName("비즈니스 로직 - 인증 코드 발송")
    @Nested
    class SendCode {

        @Test
        @DisplayName("인증 코드를 생성하고 엔티티 저장에 성공하여 인증 코드 전송에 성공한다")
        void 인증_코드_생성_저장에_성공하여_인증_코드_전송에_성공한다() {
            // given
            String certTarget = "example@naver.com";
            AuthCode authCode = mock(AuthCode.class);

            when(authCodeRepository.save(any(AuthCode.class))).thenReturn(authCode);
            doNothing().when(emailSenderService).sendEmailVerificationMail(eq(certTarget), any(String.class));

            AuthCodeServiceImpl authCodeService = new AuthCodeServiceImpl(userComponent, authCodeRepository, emailSenderService);

            // when
            AuthCodeSendCodeResponseView authCodeSendCodeResponseView = authCodeService.sendCode(certTarget);

            // then
            verify(emailSenderService, times(1)).sendEmailVerificationMail(eq(certTarget), any(String.class));
            assertThat(authCodeSendCodeResponseView.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("인증 코드 엔티티 저장에 실패하여 예외를 발생시킨다.")
        void 인증_코드_저장에_실패하여_예외를_발생시킨다() {
            // given
            String certTarget = "example@naver.com";
            int ttl = 10;

            when(authCodeRepository.save(any(AuthCode.class))).thenReturn(AuthCode.of(certTarget, "RANDOM", ttl));

            AuthCodeServiceImpl authCodeService = new AuthCodeServiceImpl(userComponent, authCodeRepository, emailSenderService);

            // when & then
            try {
                authCodeService.sendCode(certTarget);
            } catch (CustomException ex) {
                verify(emailSenderService, times(0)).sendEmailVerificationMail(any(), any());
                assertThat(ex.getErrorType()).isEqualTo(AuthErrorType.AUTH_CODE_SAVE_ERROR);
            }
        }
    }

    @DisplayName("비즈니스 로직 - 인증 코드 검증")
    @Nested
    class ValidateCode {

        @Test
        @DisplayName("요청한 이메일과 인증 코드로 검증을 요청하여, 유효한 검증 결과를 확인한다.")
        void 인증_코드_검증을_요청하여_유효한_검증_결과를_확인한다() {
            // given
            String email = "example@naver.com";
            String code = "RANDOM";

            AuthCode authCode = mock(AuthCode.class);

            when(authCodeRepository.findByEmailAndCode(email, code)).thenReturn(Optional.of(authCode));
            when(authCode.getExpiredAt()).thenReturn(LocalDateTime.now().plusMinutes(10));

            AuthCodeServiceImpl authCodeService = new AuthCodeServiceImpl(userComponent, authCodeRepository, emailSenderService);

            // when
            AuthCodeValidateCodeResponseView authCodeValidateCodeResponseView = authCodeService.validateCode(email, code);

            // then
            assertThat(authCodeValidateCodeResponseView.code()).isEqualTo(code);
            assertThat(authCodeValidateCodeResponseView.validate()).isTrue();
        }

        @Test
        @DisplayName("요청한 이메일과 인증 코드로 검증 요청을 하지만, 유효 기간 만료로 인해 실패 결과를 받는다.")
        void 인증_코드_검증을_요청_하지만_유효_기간_만료로_인해_실패_결과를_받는다() {
            // given
            String email = "example@naver.com";
            String code = "RANDOM";

            AuthCode authCode = mock(AuthCode.class);

            when(authCodeRepository.findByEmailAndCode(email, code)).thenReturn(Optional.of(authCode));
            when(authCode.isExpired()).thenReturn(true);

            AuthCodeServiceImpl authCodeService = new AuthCodeServiceImpl(userComponent, authCodeRepository, emailSenderService);

            // when
            AuthCodeValidateCodeResponseView authCodeValidateCodeResponseView = authCodeService.validateCode(email, code);

            // then
            assertThat(authCodeValidateCodeResponseView.code()).isEqualTo(code);
            assertThat(authCodeValidateCodeResponseView.validate()).isFalse();
        }

        @Test
        @DisplayName("요청한 이메일과 인증 코드로 검증 요청을 하지만, 일치하는 인증 코드를 찾지 못하여 실패 결과를 받는다.")
        void 인증_코드_검증을_요청_하지만_저장되어_있는_인증_코드를_찾지_못하여_실패_결과를_받는다() {
            // given
            String email = "example@naver.com";
            String code = "RANDOM";

            when(authCodeRepository.findByEmailAndCode(email, code)).thenReturn(Optional.empty());

            AuthCodeServiceImpl authCodeService = new AuthCodeServiceImpl(userComponent, authCodeRepository, emailSenderService);

            // when
            AuthCodeValidateCodeResponseView result = authCodeService.validateCode(email, code);

            // then
            assertThat(result.code()).isEqualTo(code);
            assertThat(result.validate()).isFalse();
        }
    }
}