package com.eventty.eventtynextgen.auth.client;

import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import org.springframework.stereotype.Component;

@Component
public class LocalAuthClient implements AuthClient {

    @Override
    public Long saveUser(AuthUser authUser) {
        return null;
    }
}
