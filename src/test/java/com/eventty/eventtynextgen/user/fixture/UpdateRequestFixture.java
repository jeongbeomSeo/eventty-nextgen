package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.request.UserUpdateRequestCommand;

public class UpdateRequestFixture {

    public static UserUpdateRequestCommand createSuccessUpdateRequest(Long id) {
        return new UserUpdateRequestCommand(id, "홍길동2", "010-1111-1111", "2020-01-01");
    }

}
