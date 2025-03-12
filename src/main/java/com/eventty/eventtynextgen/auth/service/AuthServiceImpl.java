package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.auth.client.AuthClient;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import com.eventty.eventtynextgen.auth.repository.AuthRepository;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.AuthErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final AuthClient authClient;
    private final AuthRepository authRepository;

    @Override
    @Transactional
    public Long signup(SignupRequest request) {

        if (authRepository.existsByEmail(request.getEmail())) {
            throw CustomException.badRequest(AuthErrorType.EMAIL_ALREADY_EXISTS_EXCEPTION);
        }

        AuthUser authUser = new AuthUser(request.getEmail(), request.getPassword(),
            request.getUserRole());

        // TODO: 패스워드 암호화

        authUser = authRepository.save(authUser);

        Long id = authClient.saveUser(authUser);

        return id;
    }
}
