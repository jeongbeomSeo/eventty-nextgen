package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.model.request.UpdateUserRequest;

public class UpdateUserRequestFixture {
    public static UpdateUserRequest basicUpdateRequest() {
        return new UpdateUserRequest(1L, "홍길동v2", "010-9999-9999", "2000.01.01");
    }
}
