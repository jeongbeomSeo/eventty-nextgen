package com.eventty.eventtynextgen.certification.certificationcode.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CertificationValidateCodeResponseView(
    @Schema(description = "인증 코드")
    String code,
    @Schema(description = "인증 성공 여부")
    boolean validate
) {
}
