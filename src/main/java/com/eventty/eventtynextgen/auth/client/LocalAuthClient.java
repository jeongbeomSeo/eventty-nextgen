package com.eventty.eventtynextgen.auth.client;

import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import com.eventty.eventtynextgen.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalAuthClient implements AuthClient {

    private final UserService userService;

    @Override
    public Long saveUser(Long authUserId, SignupRequest request) {
        UserSignupRequest userSignupRequest = new UserSignupRequest(authUserId,
            request.getName(), request.getPhone(), request.getBirth());

        return userService.signup(userSignupRequest);
    }
}
