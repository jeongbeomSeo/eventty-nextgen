package com.eventty.eventtynextgen.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthReissueSessionTokenRequestCommand(
    @Schema(description = "엑세스 토큰")
    @NotBlank(message = "엑세스 토큰은 필수값입니다.")
    String accessToken
) {
}
