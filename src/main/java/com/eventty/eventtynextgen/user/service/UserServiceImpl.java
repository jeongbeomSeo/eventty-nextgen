package com.eventty.eventtynextgen.user.service;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.UserErrorType;
import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import com.eventty.eventtynextgen.user.model.entity.User;
import com.eventty.eventtynextgen.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final JpaUserRepository userRepository;

    @Override
    public Long signup(@Validated UserSignupRequest userSignupRequest) {

        if (userRepository.existsByAuthUserId(userSignupRequest.getAuthUserId())) {
            throw CustomException.of(HttpStatus.CONFLICT, UserErrorType.AUTH_USER_ID_ALREADY_EXISTS);
        }

        User user = new User(userSignupRequest.getAuthUserId(), userSignupRequest.getName(),
            userSignupRequest.getPhone(), userSignupRequest.getBirth());
        user = userRepository.save(user);

        return user.getId();
    }
}
