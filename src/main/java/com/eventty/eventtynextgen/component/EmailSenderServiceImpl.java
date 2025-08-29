package com.eventty.eventtynextgen.component;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.MailErrorType;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendEmailVerificationMail(String receiver, String code) {
        MailType mailType = MailType.EMAIL_VERIFICATION;

        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            msgHelper.setTo(receiver);
            msgHelper.setSubject(mailType.getSubject());
            msgHelper.setText(setContext(code, mailType.getTemplate()), true);
            this.javaMailSender.send(mimeMessage);

            log.info("Succeeded to send email");
        } catch (Exception e) {
            log.error("Failed to send email verification mail, message={}", e.getMessage());
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, MailErrorType.FAILED_SEND_TO_EMAIL_VERIFICATION_MAIL);
        }
    }

    private String setContext(String code, String template) {
        Context context = new Context();
        context.setVariable("code", code);
        return this.templateEngine.process(template, context);
    }

    @Getter
    @RequiredArgsConstructor
    private enum MailType {
        EMAIL_VERIFICATION("email-verification", "이메일 인증"),
        ;

        private final String template;
        private final String subject;
    }
}
