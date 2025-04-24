package com.eventty.eventtynextgen.component;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class TestEmailSenderService implements EmailSenderService{

    @Override
    public void sendEmailVerificationMail(String receiver, String code) {
        System.out.println("[테스트] 이메일 전송 성공");
    }
}
