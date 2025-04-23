package com.eventty.eventtynextgen.certification.response;

import lombok.Getter;

@Getter
public class CertificationSendCodeResponseView {

    private final String message;

    private CertificationSendCodeResponseView(String message) {
        this.message = message;
    }

    public static CertificationSendCodeResponseView createMessage(String email, int ttl) {
        String message = String.format("%s로 인증 코드를 발송했습니다. %d분 안에 인증 코드를 입력해 주세요.", email, ttl);

        return new CertificationSendCodeResponseView(message);
    }
}
