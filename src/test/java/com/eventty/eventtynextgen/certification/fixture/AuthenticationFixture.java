package com.eventty.eventtynextgen.certification.fixture;

import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.GrantedAuthority;
import com.eventty.eventtynextgen.certification.core.autority.SimpleGrantedAuthority;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AuthenticationFixture {

    public static Authentication createUnauthenticatedLoginIdPasswordAuthentication() {
        return LoginIdPasswordAuthenticationToken.unauthenticated(UserDetailsFixture.createLoginIdUserDetailsFromCredentials());
    }

    public static Authentication createAuthenticatedLoginIdPasswordAuthentication() {
        return LoginIdPasswordAuthenticationToken.unauthenticated(UserDetailsFixture.createLoginIdUserDetailsFromPrincipal());
    }

    public static Authentication createAuthorizedLoginIdPasswordAuthentication(Long userId, String loginId, String plainPassword) {
        UserDetails userDetails = UserDetailsFixture.createLoginIdUserDetailsFromPrincipal(userId, loginId, plainPassword);
        Authentication authenticated = LoginIdPasswordAuthenticationToken.authenticated(userDetails);
        Collection<? extends GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(UserRoleType.USER.name()));
        return LoginIdPasswordAuthenticationToken.authorized(authenticated, grantedAuthorities);
    }
}
