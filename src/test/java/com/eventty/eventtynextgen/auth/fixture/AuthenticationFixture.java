package com.eventty.eventtynextgen.auth.fixture;

import com.eventty.eventtynextgen.auth.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.auth.core.Authentication;
import com.eventty.eventtynextgen.auth.core.GrantedAuthority;
import com.eventty.eventtynextgen.auth.core.autority.SimpleGrantedAuthority;
import com.eventty.eventtynextgen.auth.core.userdetails.UserDetails;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import java.util.Collection;
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
