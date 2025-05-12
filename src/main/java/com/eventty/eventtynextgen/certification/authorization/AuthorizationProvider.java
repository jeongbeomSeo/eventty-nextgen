package com.eventty.eventtynextgen.certification.authorization;

import com.eventty.eventtynextgen.certification.core.Authentication;

public interface AuthorizationProvider {

    Authentication authorize(Authentication authentication);
}
