package com.eventty.eventtynextgen.certification.fixture;

import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.core.Authentication;

public class AuthenticationFixture {

    public static Authentication createUnauthenticatedLoginIdPasswordAuthentication() {
        return LoginIdPasswordAuthenticationToken.unauthenticated(UserDetailsFixture.createLoginIdUserDetailsFromCredentials());
    }

    public static Authentication createAuthenticatedLoginIdPasswordAuthentication() {
        return LoginIdPasswordAuthenticationToken.unauthenticated(UserDetailsFixture.createLoginIdUserDetailsFromPrincipal());
    }

    public static Authentication createAuthorizedLoginIdPasswordAuthentication() {
        return LoginIdPasswordAuthenticationToken.unauthenticated(UserDetailsFixture.createLoginIdUserDetailsFromPrincipal());
    }
}
