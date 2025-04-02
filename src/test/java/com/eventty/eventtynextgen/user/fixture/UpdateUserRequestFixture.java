package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.request.UserUpdateRequestCommand;

public class UpdateUserRequestFixture {
    public static UserUpdateRequestCommand basicUpdateRequest() {
        return new UserUpdateRequestCommand(1L, "홍길동v2", "010-9999-9999", "2000.01.01");
    }
}
