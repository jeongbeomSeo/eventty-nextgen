package com.eventty.eventtynextgen.auth.client;

import com.eventty.eventtynextgen.auth.model.entity.AuthUser;

public interface AuthClient {
    Long saveUser(AuthUser authUser);
}
