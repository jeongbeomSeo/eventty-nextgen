package com.eventty.eventtynextgen.certification.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CertificationLoginRequestCommand(
    @Schema(description = "사용자 ID")
    String loginId,
    @Schema(description = "사용자 패스워드")
    String password
){
}
