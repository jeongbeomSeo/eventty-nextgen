package com.eventty.eventtynextgen.user.response;

public record UserActivateDeletedUserResponseView(
    Long userId,
    String email,
    String name
) {
}
