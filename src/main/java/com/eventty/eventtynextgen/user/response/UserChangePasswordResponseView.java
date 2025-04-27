package com.eventty.eventtynextgen.user.response;

public record UserChangePasswordResponseView(
    Long userId,
    String name,
    String email
) {

}
