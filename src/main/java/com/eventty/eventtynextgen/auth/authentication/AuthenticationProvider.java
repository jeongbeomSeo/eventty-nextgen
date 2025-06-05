package com.eventty.eventtynextgen.auth.authentication;

import com.eventty.eventtynextgen.auth.core.Authentication;

public interface AuthenticationProvider {

    Authentication authenticate(Authentication authentication);
}
