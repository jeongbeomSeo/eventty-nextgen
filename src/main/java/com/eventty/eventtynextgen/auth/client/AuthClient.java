package com.eventty.eventtynextgen.auth.client;

import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;

public interface AuthClient {

    Long saveUser(UserSignupRequest userSignupRequest);

    Long deleteUser(Long authUserId);
}
