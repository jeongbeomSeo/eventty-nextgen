package com.eventty.eventtynextgen.certification.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CertificationReissueResponseView(
    @Schema(description = "사용자 PK")
    Long userId,
    @Schema(description = "토큰 정보")
    AccessTokenInfo accessTokenInfo
) {
    public record AccessTokenInfo(
        @Schema(description = "토큰 타입")
        String tokenType,
        @Schema(description = "엑세스 토큰")
        String accessToken
    ){}
}
