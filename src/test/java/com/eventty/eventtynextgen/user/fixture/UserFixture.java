package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.entity.enumtype.UserRole;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.request.UserSignupRequestCommand;
import com.eventty.eventtynextgen.user.request.UserUpdateRequestCommand;

public class UserFixture {

    public static User createUserBySignupRequest(UserSignupRequestCommand request) {
        return new User(1L, request.getEmail(), request.getPassword(), request.getUserRole(),
            request.getName(), request.getPhone(), request.getBirth(), false, null);
    }

    public static User createBaseUser() {
        return new User(1L, "example@naver.com", "hashed_password", UserRole.USER, "홍길동",
            "000-0000-0000", "2000-01-01", false, null);
    }

    public static User createUserByUpdateUserRequest(UserUpdateRequestCommand request) {
        return new User(1L, "example@naver.com", "hashed_password", UserRole.USER,
            request.getName(), request.getPhone(), request.getBirth(), false, null);
    }
}
