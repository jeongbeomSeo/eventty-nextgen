package com.eventty.eventtynextgen.certification.request;

import com.eventty.eventtynextgen.user.shared.annotation.EmailRegexp;
import jakarta.validation.constraints.NotBlank;

public record CertificationValidateRequestCommand(
    @EmailRegexp
    String email,
    @NotBlank(message = "인증 코드는 필수입니다.")
    String code
) {
}
