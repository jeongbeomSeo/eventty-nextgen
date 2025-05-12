package com.eventty.eventtynextgen.certification.core;

import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import java.util.Collection;

public interface Authentication {

    Collection<? extends GrantAuthority> getAuthorities();

    UserDetails getUserDetails();

    boolean isAuthenticated();
}
