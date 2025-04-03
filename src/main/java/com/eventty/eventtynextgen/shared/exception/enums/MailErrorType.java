package com.eventty.eventtynextgen.shared.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailErrorType implements ErrorType {

    FAILED_SEND_TO_EMAIL_VERIFICATION_MAIL("FAILED_SEND_TO_EMAIL_VERIFICATION_MAIL", "이메일 검증 코드 전송에 실패했습니다."),
    ;

    private final String code;
    private final String msg;
}
