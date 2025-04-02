package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.entity.enumtype.UserRole;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.request.UserRequestCommand;

public class UserFixture {

    public static User createUserBySignupRequest(UserRequestCommand userSignupRequestCommand) {
        return new User(1L, userSignupRequestCommand.email(), userSignupRequestCommand.password(), userSignupRequestCommand.userRole(),
            userSignupRequestCommand.name(), userSignupRequestCommand.phone(), userSignupRequestCommand.birth(), false, null);
    }

    public static User createBaseUser(Long userId) {
        return new User(userId, "example@naver.com", "hashed_password", UserRole.USER, "홍길동",
            "000-0000-0000", "2000-01-01", false, null);
    }

    public static User createUserFromDb(String email, String hashedPassword, UserRole userRole,
        String name, String phone, String birth) {
        return new User(1L, email, hashedPassword, userRole, name, phone, birth, false, null);
    }

    public static User createBaseUser(Long userId, String name, String phone, String birth) {
        return new User(userId, "example@naver.com", "hashed_password", UserRole.USER, name, phone,
            birth, false, null);
    }
}
