package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;

public interface AuthService {

    Long signup(SignupRequest signupRequest);

    boolean checkEmail(String email);

    Long delete(Long authUserId);
}
