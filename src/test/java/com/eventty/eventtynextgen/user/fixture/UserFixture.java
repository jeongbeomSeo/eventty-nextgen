package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.model.UserRole;
import com.eventty.eventtynextgen.user.model.entity.User;
import com.eventty.eventtynextgen.user.model.request.SignupRequest;
import com.eventty.eventtynextgen.user.model.request.UpdateUserRequest;

public class UserFixture {

    public static User createUserBySignupRequest(SignupRequest request) {
        return new User(1L, request.getEmail(), request.getPassword(), request.getUserRole(),
            request.getName(), request.getPhone(), request.getBirth(), false, null);
    }

    public static User createBaseUser() {
        return new User(1L, "example@naver.com", "hashed_password", UserRole.USER, "홍길동",
            "000-0000-0000", "2000-01-01", false, null);
    }

    public static User createUserByUpdateUserRequest(UpdateUserRequest request) {
        return new User(1L, "example@naver.com", "hashed_password", UserRole.USER,
            request.getName(), request.getPhone(), request.getBirth(), false, null);
    }
}
