package com.eventty.eventtynextgen.config;

import com.eventty.eventtynextgen.config.properties.MailProperties;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailProperties mailProperties;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(mailProperties.getHost());
        javaMailSender.setUsername(mailProperties.getUsername());
        javaMailSender.setPassword(mailProperties.getPassword());
        javaMailSender.setPort(mailProperties.getPort());

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", mailProperties.isAuth());
        properties.put("mail.smtp.debug", mailProperties.isDebug());
        properties.put("mail.smtp.connectiontimeout", mailProperties.isDebug());
        properties.put("mail.smtp.starttls.enable", mailProperties.isStartTlsEnable());

        javaMailSender.setJavaMailProperties(properties);
        javaMailSender.setDefaultEncoding(mailProperties.getDefaultEncoding());

        return javaMailSender;
    }


}
