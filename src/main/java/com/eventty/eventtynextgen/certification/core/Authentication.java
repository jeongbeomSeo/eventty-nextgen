package com.eventty.eventtynextgen.certification.core;

import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import java.util.Collection;

public interface Authentication {

    Collection<? extends GrantedAuthority> getAuthorities();

    UserDetails getUserDetails();

    boolean isAuthenticated();

    boolean isAuthorized();
}
