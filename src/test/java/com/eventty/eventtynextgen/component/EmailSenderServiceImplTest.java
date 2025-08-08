package com.eventty.eventtynextgen.component;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@ExtendWith(MockitoExtension.class)
@DisplayName("Email Sender ServiceImpl 클래스 단위 테스트")
class EmailSenderServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpringTemplateEngine springTemplateEngine;

    @Nested
    @DisplayName("인증 코드 이메일 전송 테스트")
    class SendEmailVerificationMail {

        @Test
        @DisplayName("이메일 전송에 필요한 세팅을 성공적으로 마칠 경우 메시지 전송 함수 호출에 성공한다.")
        void 이메일_전송에_필요한_세팅을_성공적으로_마칠_경우_메시지_전송_함수_호출에_성공한다() {
            // given
            String receiver = "jeongbeom4693@gmail.com";
            String code = "123456";

            MimeMessage mimeMessage = mock(MimeMessage.class);

            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(springTemplateEngine.process(anyString(), any(Context.class))).thenReturn("test");
            doNothing().when(javaMailSender).send(mimeMessage);

            EmailSenderServiceImpl emailSenderService = new EmailSenderServiceImpl(javaMailSender, springTemplateEngine);

            // when
            emailSenderService.sendEmailVerificationMail(receiver, code);

            // then
            verify(javaMailSender, times(1)).send(mimeMessage);
        }
    }
}