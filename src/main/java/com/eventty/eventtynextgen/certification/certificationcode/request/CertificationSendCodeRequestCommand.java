package com.eventty.eventtynextgen.certification.certificationcode.request;

import com.eventty.eventtynextgen.user.annotation.EmailRegexp;
import io.swagger.v3.oas.annotations.media.Schema;

public record CertificationSendCodeRequestCommand(
    @Schema(description = "사용자 이메일", pattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    @EmailRegexp
    String email
) {

}
