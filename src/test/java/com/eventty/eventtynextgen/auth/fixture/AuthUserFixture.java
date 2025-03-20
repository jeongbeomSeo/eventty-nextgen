package com.eventty.eventtynextgen.auth.fixture;

import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.model.entity.AuthUser;

public class AuthUserFixture {

    public static AuthUser createAuthUserBySignupRequest(SignupRequest request) {
        return new AuthUser(1L, request.getEmail(), request.getPassword(),
            request.getUserRole(), false, null);
    }
}
