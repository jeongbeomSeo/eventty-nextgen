package com.eventty.eventtynextgen.certification.authentication;

import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.GrantedAuthority;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.springframework.util.Assert;

public class LoginIdPasswordAuthenticationToken implements Authentication {

    private final Collection<GrantedAuthority> authorities;
    private final UserDetails userDetails;


    private LoginIdPasswordAuthenticationToken(UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        this.userDetails = userDetails;
        this.authorities = Collections.unmodifiableList(new ArrayList<>(authorities));
    }

    public static Authentication unauthenticated(UserDetails userDetails) {
        Assert.notNull(userDetails, "userDetails must not be null");
        return new LoginIdPasswordAuthenticationToken(userDetails, Collections.emptyList());
    }

    public static Authentication authenticated(UserDetails userDetails) {
        Assert.notNull(userDetails, "userDetails must not be null");
        Assert.isTrue(userDetails.isIdentified(), "An unidentified user cannot create a token.");
        return new LoginIdPasswordAuthenticationToken(userDetails, Collections.emptyList());
    }

    public static Authentication authorized(Authentication authentication, Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authentication, "authentication must not be null");
        Assert.isTrue(authentication.isAuthenticated(), "Only an authenticated user can create an authorized token.");
        Assert.isTrue(authentication.getAuthorities().isEmpty(), "A user who is already authorized cannot create a new token");
        return new LoginIdPasswordAuthenticationToken(authentication.getUserDetails(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public UserDetails getUserDetails() {
        return this.userDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return this.getUserDetails().isIdentified();
    }

    @Override
    public boolean isAuthorized() {
        return !this.authorities.isEmpty();
    }
}
