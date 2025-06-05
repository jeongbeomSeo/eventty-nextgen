package com.eventty.eventtynextgen.auth.authcode.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthCodeExistsEmailResponseView(
    @Schema(description = "사용자 이메일")
    String email,
    @Schema(description = "이메일 존재 여부")
    boolean exists
) {
}
