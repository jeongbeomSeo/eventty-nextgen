package com.eventty.eventtynextgen.certification.authentication;

import com.eventty.eventtynextgen.certification.core.Authentication;

public interface AuthenticationProvider {

    Authentication authenticate(Authentication authentication);
}
