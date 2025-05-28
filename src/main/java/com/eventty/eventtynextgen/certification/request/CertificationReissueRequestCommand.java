package com.eventty.eventtynextgen.certification.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CertificationReissueRequestCommand(

    @Schema(description = "사용자 PK")
    @NotNull(message = "사용자 ID는 필수값입니다.")
    Long userId,
    @Schema(description = "엑세스 토큰")
    @NotBlank(message = "엑세스 토큰은 필수값입니다.")
    String accessToken
) {
}
