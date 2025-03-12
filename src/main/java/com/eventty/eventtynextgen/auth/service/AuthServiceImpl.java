package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.auth.client.AuthClient;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final AuthClient authClient;
    private final AuthRepository authRepository;

    @Override
    public Long signup(SignupRequest signupRequest) {


        return null;
    }
}
