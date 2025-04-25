package com.eventty.eventtynextgen.user.response;

public record UserActivateDeletedUserResponseView(
    Long id,
    String email,
    String name
) {
}
