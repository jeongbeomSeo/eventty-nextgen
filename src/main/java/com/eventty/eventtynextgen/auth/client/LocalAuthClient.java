package com.eventty.eventtynextgen.auth.client;

import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import com.eventty.eventtynextgen.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalAuthClient implements AuthClient {

    private final UserService userService;

    @Override
    public Long saveUser(UserSignupRequest request) {
        UserSignupRequest userSignupRequest = new UserSignupRequest(request.getAuthUserId(),
            request.getName(), request.getPhone(), request.getBirth());

        return userService.signup(userSignupRequest);
    }
}
