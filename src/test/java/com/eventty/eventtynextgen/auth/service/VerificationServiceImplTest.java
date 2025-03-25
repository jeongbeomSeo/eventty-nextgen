package com.eventty.eventtynextgen.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.auth.model.dto.request.EmailVerificationRequest;
import com.eventty.eventtynextgen.auth.model.dto.request.EmailVerificationValidationRequest;
import com.eventty.eventtynextgen.auth.model.dto.response.EmailVerificationResponse;
import com.eventty.eventtynextgen.auth.redis.EmailVerificationService;
import com.eventty.eventtynextgen.auth.redis.entity.EmailVerification;
import com.eventty.eventtynextgen.auth.repository.JpaAuthRepository;
import com.eventty.eventtynextgen.auth.service.utils.CodeGenerator;
import com.eventty.eventtynextgen.auth.service.utils.EmailService;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.AuthErrorType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerificationService 단위 테스트")
class VerificationServiceImplTest {

    @Mock
    private CodeGenerator codeGenerator;

    @Mock
    private JpaAuthRepository authRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private EmailService emailService;

    @DisplayName("비즈니스 로직 - 이메일 검증")
    @Nested
    class EmailVerificationTest {

        @Test
        @DisplayName("email verification code - 인증 코드를 올바르게 저장하고, 인증 코드 발송까지 성공한다.")
        void 인증_코드를_저장한_뒤_이메일로_인증_코드_발송에_성공한다() {
            // given
            String email = "jeongbeom4693@gmail.com";
            EmailVerificationRequest request = new EmailVerificationRequest(email);
            String code = "ABCDEF";
            EmailVerification emailVerification = EmailVerification.createEntity(email, code);

            when(codeGenerator.generateVerificationCode(code.length())).thenReturn(code);
            when(emailVerificationService.saveEmailVerification(email, code)).thenReturn(
                emailVerification);
            doNothing().when(emailService).sendEmailVerificationMail(email, code);

            VerificationService verificationService = new VerificationServiceImpl(authRepository,
                codeGenerator, emailVerificationService, emailService);

            // when
            EmailVerificationResponse result = verificationService.sendEmailVerificationCode(
                request);

            // then
            assertThat(result.getMessage()).isNotBlank();
        }

        // TODO: 이메일 검증 요청시 올바르게 검증 성공
        @Test
        @DisplayName("check validation email - 이메일 검증 요청시 유효 기간 내에 올바른 email과 code로 요청할 경우 성공한다.")
        void 유효_기간_내에_올바른_이메일과_코드로_요청할_경우_성공한다() {
            // given
            String email = "jeongbeom4693@gmail.com";
            String code = "ABCDEF";
            EmailVerificationValidationRequest request = new EmailVerificationValidationRequest(
                email, code);
            EmailVerification emailVerification = EmailVerification.createEntity(email, code);

            when(emailVerificationService.findEmailVerification(email)).thenReturn(
                Optional.of(emailVerification));
            doNothing().when(emailVerificationService).deleteEmailVerification(emailVerification);

            VerificationService verificationService = new VerificationServiceImpl(authRepository,
                codeGenerator, emailVerificationService, emailService);

            // when
            boolean result = verificationService.checkValidationEmail(request);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("check validation email - 이메일 검증 요청시 유효 기간이 지났다면 예외를 발생시킨다.")
        void 유효_기간이_지났을_경우_예외를_발생시킨다() {
            // given
            String email = "jeongbeom4693@gmail.com";
            String code = "ABCDEF";
            EmailVerificationValidationRequest request = new EmailVerificationValidationRequest(
                email, code);

            when(emailVerificationService.findEmailVerification(email)).thenReturn(
                Optional.empty());

            VerificationService verificationService = new VerificationServiceImpl(authRepository,
                codeGenerator, emailVerificationService, emailService);

            // when & then
            try {
                verificationService.checkValidationEmail(request);
            } catch (CustomException exception) {
                assertThat(exception.getErrorType()).isEqualTo(
                    AuthErrorType.EXPIRE_EMAIL_VERIFICATION_CODE);
            }
        }

        // TODO: 이메일 검증 요청시 code가 일치하지 않는 경우
        @Test
        @DisplayName("check validation email - 이메일 검증 요청시 입력값과 code가 일치하지 않는 경우 예외를 발생시킨다.")
        void 입력값과_저장되어_있는_코드가_일치하지_않을_경우_예외를_발생시킨다() {
            // given
            String email = "jeongbeom4693@gmail.com";
            String code = "ABCDEF";
            EmailVerificationValidationRequest request = new EmailVerificationValidationRequest(
                email, code);
            EmailVerification emailVerification = EmailVerification.createEntity(email, code);

            when(emailVerificationService.findEmailVerification(email)).thenReturn(
                Optional.of(emailVerification));

            VerificationService verificationService = new VerificationServiceImpl(authRepository,
                codeGenerator, emailVerificationService, emailService);

            // when & then
            try {
                verificationService.checkValidationEmail(request);
            } catch (CustomException exception) {
                assertThat(exception.getErrorType()).isEqualTo(
                    AuthErrorType.MISMATCH_EMAIL_VERIFICATION_CODE);
            }
        }
    }

}