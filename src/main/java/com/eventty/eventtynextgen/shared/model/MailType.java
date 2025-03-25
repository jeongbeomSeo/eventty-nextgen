package com.eventty.eventtynextgen.shared.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailType {

    EMAIL_VERIFICATION("email-verification", "이메일 인증"),
    ;

    private final String template;
    private final String subject;
}
