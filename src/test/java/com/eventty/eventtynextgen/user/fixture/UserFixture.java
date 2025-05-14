package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;

public class UserFixture {

    public static User createUser() {
        return User.of("test@naver.com", "password", UserRoleType.USER, "name", "000-0000-0000", "2000-01-01");
    }
    public static User createUserByEmail(String email) {
        return User.of(email, "password", UserRoleType.USER, "name", "000-0000-0000", "2000-01-01");
    }

    public static User createUserByEmailAndNameAndPhone(String email, String name, String phone) {
        return User.of(email, "password", UserRoleType.USER, name, phone, "2000-01-01");
    }
    public static User createUserByNameAndPhone(String name, String phone) {
        return User.of("test@naver.com", "password", UserRoleType.USER, name, phone, "2000-01-01");
    }

    public static User createUserByPassword(String password) {
        return User.of("test@naver.com", password, UserRoleType.USER, "name", "000-0000-0000", "2000-01-01");
    }

    public static User createUserByCredentials(String email, String password) {
        return User.of(email, password, UserRoleType.USER, "name", "000-0000-0000", "2000-01-01");
    }
}
