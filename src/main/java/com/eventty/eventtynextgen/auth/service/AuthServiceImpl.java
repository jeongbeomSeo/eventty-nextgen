package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.auth.client.AuthClient;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import com.eventty.eventtynextgen.auth.repository.JpaAuthRepository;
import com.eventty.eventtynextgen.auth.service.utils.PasswordEncoder;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.AuthErrorType;
import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Component
public class AuthServiceImpl implements AuthService {

    private final AuthClient authClient;
    private final JpaAuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Long signup(SignupRequest request) {

        if (authRepository.existsByEmail(request.getEmail())) {
            throw CustomException.of(HttpStatus.CONFLICT, AuthErrorType.EMAIL_ALREADY_EXISTS);
        }

        String hashedPassword = passwordEncoder.hashPassword(request.getPassword());

        AuthUser authUser = new AuthUser(request.getEmail(), hashedPassword, request.getUserRole());
        authUser = authRepository.save(authUser);

        Long id = authClient.saveUser(new UserSignupRequest(authUser.getId(), request));

        return id;
    }

    @Override
    public boolean checkEmail(String email) {
        return authRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public Long delete(Long authUserId) {
        return null;
    }
}
