package com.eventty.eventtynextgen.certification.authorization;

import com.eventty.eventtynextgen.certification.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserRoleAuthorizationProvider implements AuthorizationProvider {

    @Override
    public Authentication authorize(Authentication authentication) {
        return null;
    }
}
