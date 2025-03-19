package com.eventty.eventtynextgen.user.service;

import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;

public interface UserService {

    Long signup(UserSignupRequest userSignupRequest);
}
