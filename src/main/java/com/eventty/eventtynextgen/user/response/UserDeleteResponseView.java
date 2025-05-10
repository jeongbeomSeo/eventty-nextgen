package com.eventty.eventtynextgen.user.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDeleteResponseView(
    @Schema(description = "사용자 PK")
    Long userId
) {
}
