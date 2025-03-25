package com.eventty.eventtynextgen.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.auth.model.dto.response.EmailVerificationResponse;
import com.eventty.eventtynextgen.auth.redis.EmailVerificationService;
import com.eventty.eventtynextgen.auth.redis.entity.EmailVerification;
import com.eventty.eventtynextgen.auth.repository.JpaAuthRepository;
import com.eventty.eventtynextgen.auth.service.utils.CodeGenerator;
import com.eventty.eventtynextgen.auth.service.utils.EmailService;
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
        @DisplayName("auth email verification - 인증 코드를 올바르게 저장하고, 인증 코드 발송까지 성공한다.")
        void 인증_코드를_저장한_뒤_이메일로_인증_코드_발송에_성공한다() {
            // given
            String email = "jeongbeom4693@gmail.com";
            String code = "ABCDEF";
            EmailVerification emailVerification = new EmailVerification(email, code);

            when(codeGenerator.generateVerificationCode(code.length())).thenReturn(code);
            when(emailVerificationService.saveEmailVerification(email, code)).thenReturn(
                emailVerification);
            doNothing().when(emailService).sendEmailVerificationMail(email, code);

            VerificationService verificationService = new VerificationServiceImpl(authRepository,
                codeGenerator, emailVerificationService, emailService);

            // when
            EmailVerificationResponse result = verificationService.sendEmailVerificationCode(
                email);

            // then
            assertThat(result.getMessage()).isNotBlank();
        }

        // TODO: 이메일 검증 요청시 올바르게 검증 성공

        // TODO: 이메일 검증 요청시 TTL이 초과된 경우

        // TODO: 이메일 검증 요청시 code가 일치하지 않는 경우

        // TODO: 이메일 검증 요청시 email을 찾을 수 없는 경우
    }

}