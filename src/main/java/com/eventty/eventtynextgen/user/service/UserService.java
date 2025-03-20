package com.eventty.eventtynextgen.user.service;

import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import com.eventty.eventtynextgen.user.model.request.UpdateUserRequest;

public interface UserService {

    Long signup(UserSignupRequest userSignupRequest);

    Long updateUser(UpdateUserRequest updateUserRequest);
}
