package com.eventty.eventtynextgen.auth.fixture;

import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import com.eventty.eventtynextgen.user.model.entity.User;

public class UserFixture {

    public static User createUserByUserSignupRequest(UserSignupRequest request) {
        return new User(1L, request.getAuthUserId(), request.getName(), request.getPhone(),
            request.getBirth());
    }

}
