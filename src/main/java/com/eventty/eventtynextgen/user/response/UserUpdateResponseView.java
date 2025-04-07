package com.eventty.eventtynextgen.user.response;

public record UserUpdateResponseView (
    Long userId,
    String name,
    String phone,
    String birth
){
}
