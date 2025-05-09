package com.eventty.eventtynextgen.user.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserUpdateResponseView (
    @Schema(description = "사용자 PK")
    Long userId,
    @Schema(description = "사용자 이름")
    String name,
    @Schema(description = "사용자 전화번호")
    String phone,
    @Schema(description = "사용자 생년월일")
    String birth
){
}
