package com.eventty.eventtynextgen.auth.fixture;

import com.eventty.eventtynextgen.auth.model.UserRole;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.model.entity.AuthUser;

public class AuthUserFixture {

    public static AuthUser createAuthUserBySignupRequest(SignupRequest request) {
        return new AuthUser(1L, request.getEmail(), request.getPassword(),
            request.getUserRole(), false, null);
    }

    public static AuthUser createAuthUser(Long authId) {
        return new AuthUser(authId, "basic@gmail.com", "12345678", UserRole.USER, false, null);
    }
}
