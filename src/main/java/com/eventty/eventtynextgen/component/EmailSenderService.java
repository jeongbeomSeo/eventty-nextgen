package com.eventty.eventtynextgen.component;

public interface EmailSenderService {

    void sendEmailVerificationMail(String receiver, String code);
}
