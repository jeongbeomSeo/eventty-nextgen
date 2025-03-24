package com.eventty.eventtynextgen.auth.model.dto.response;

import lombok.Getter;

@Getter
public class EmailVerificationResponse {

    private final String message;

    private EmailVerificationResponse(String message) {
        this.message = message;
    }

    public static EmailVerificationResponse createMessage(String email, int ttl) {
        String message = String.format("%s로 인증 코드를 발송했습니다. %d안에 인증 코드를 입력해 주세요.", email, ttl);

        return new EmailVerificationResponse(message);
    }
}
