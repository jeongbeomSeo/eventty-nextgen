package com.eventty.eventtynextgen.auth.core;

import com.eventty.eventtynextgen.auth.core.userdetails.UserDetails;
import java.util.Collection;

public interface Authentication {

    Collection<? extends GrantedAuthority> getAuthorities();

    UserDetails getUserDetails();

    boolean isAuthenticated();

    boolean isAuthorized();
}
