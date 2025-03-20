package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import com.eventty.eventtynextgen.user.model.entity.User;
import com.eventty.eventtynextgen.user.model.request.UpdateUserRequest;

public class UserFixture {

    public static User createUserByUserSignupRequest(UserSignupRequest request) {
        return new User(1L, request.getAuthUserId(), request.getName(), request.getPhone(),
            request.getBirth(), false, null);
    }

    public static User createUserByUpdateUserRequest(UpdateUserRequest request) {
        return new User(request.getId(), 1L, request.getName(), request.getPhone(),
            request.getBirth(), false, null);
    }
}
