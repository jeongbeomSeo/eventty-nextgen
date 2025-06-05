package com.eventty.eventtynextgen.auth.authcode.fixture;

import com.eventty.eventtynextgen.auth.authcode.entity.AuthCode;

public class AuthCodeFixture {

    public static AuthCode createAuthCode(String email, String code, int ttl) {
        return AuthCode.of(email, code, ttl);
    }

}
