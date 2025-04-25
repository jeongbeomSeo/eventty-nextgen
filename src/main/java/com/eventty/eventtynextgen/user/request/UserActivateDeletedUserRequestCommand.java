package com.eventty.eventtynextgen.user.request;

import jakarta.validation.constraints.NotNull;

public record UserActivateDeletedUserRequestCommand(
    @NotNull(message = "userId는 필수값 입니다.")
    Long userId
) {
}
