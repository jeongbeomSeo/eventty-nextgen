package com.eventty.eventtynextgen.user.service;

import com.eventty.eventtynextgen.user.model.request.SignupRequest;
import com.eventty.eventtynextgen.user.model.request.UpdateUserRequest;

public interface UserService {

    Long signup(SignupRequest signupRequest);

    Long updateUser(UpdateUserRequest updateUserRequest);

    Long delete(Long userId);
}
