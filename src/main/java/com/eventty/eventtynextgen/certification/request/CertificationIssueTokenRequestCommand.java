package com.eventty.eventtynextgen.certification.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CertificationIssueTokenRequestCommand(
    @Schema(description = "애플리케이션(서비스) 이름")
    @NotBlank
    String appName,
    @Schema(description = "API 호출 비밀 키")
    @NotBlank
    String appSecret
) {

}
