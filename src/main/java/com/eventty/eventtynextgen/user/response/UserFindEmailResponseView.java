package com.eventty.eventtynextgen.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UserFindEmailResponseView(
    @Schema(description = "이름과 전화번호를 통해 찾아낸 모든 이메일")
    List<UserEmailInfo> userEmailInfos
) {
    @Schema(description = "찾아낸 사용자 정보")
    public record UserEmailInfo(
        @Schema(description = "사용자 PK")
        Long userId,
        @Schema(description = "사용자 이메일")
        String email
    ) {
    }
}