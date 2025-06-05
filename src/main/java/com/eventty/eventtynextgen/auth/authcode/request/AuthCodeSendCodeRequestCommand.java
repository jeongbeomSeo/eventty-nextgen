package com.eventty.eventtynextgen.auth.authcode.request;

import com.eventty.eventtynextgen.user.annotation.EmailRegexp;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthCodeSendCodeRequestCommand(
    @Schema(description = "사용자 이메일", pattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    @EmailRegexp
    String email
) {

}
