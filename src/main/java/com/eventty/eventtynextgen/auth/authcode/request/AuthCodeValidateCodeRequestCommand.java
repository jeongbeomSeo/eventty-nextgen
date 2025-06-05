package com.eventty.eventtynextgen.auth.authcode.request;

import com.eventty.eventtynextgen.user.annotation.EmailRegexp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthCodeValidateCodeRequestCommand(
    @Schema(description = "사용자 이메일", pattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    @EmailRegexp
    String email,
    @Schema(description = "사용자가 입력한 인증 코드")
    @NotBlank(message = "인증 코드는 필수입니다.")
    String code
) {
}
