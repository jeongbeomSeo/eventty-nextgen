package com.eventty.eventtynextgen.user.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserChangePasswordResponseView(
    @Schema(description = "사용자 PK")
    Long userId,
    @Schema(description = "사용자 이름")
    String name,
    @Schema(description = "사용자 이메일")
    String email
) {

}
