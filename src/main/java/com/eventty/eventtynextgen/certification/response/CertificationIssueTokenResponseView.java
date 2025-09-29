package com.eventty.eventtynextgen.certification.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CertificationIssueTokenResponseView (
    @Schema(description = "토큰 타입")
    String tokenType,
    @Schema(description = "토큰 값")
    String tokenValue
){
}
