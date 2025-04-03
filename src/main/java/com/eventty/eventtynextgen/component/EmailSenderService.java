package com.eventty.eventtynextgen.component;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.MailErrorType;
import com.eventty.eventtynextgen.shared.enums.MailType;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

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
            log.error("Failed to send email verification mail");
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, MailErrorType.FAILED_SEND_TO_EMAIL_VERIFICATION_MAIL);
        }
    }

    private String setContext(String code, String template) {
        Context context = new Context();
        context.setVariable("code", code);
        return this.templateEngine.process(template, context);
    }

}
