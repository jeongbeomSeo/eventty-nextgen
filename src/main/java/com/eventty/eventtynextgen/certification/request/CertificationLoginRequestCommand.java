package com.eventty.eventtynextgen.certification.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CertificationLoginRequestCommand(
    @Schema(description = "사용자 ID")
    @NotBlank(message = "사용자 ID는 필수값입니다.")
    String loginId,
    @Schema(description = "사용자 패스워드")
    @NotBlank(message = "사용자 PW는 필수값입니다.")
    String password
){
}
