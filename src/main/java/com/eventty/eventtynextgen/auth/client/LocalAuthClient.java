package com.eventty.eventtynextgen.auth.client;

import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import org.springframework.stereotype.Component;

@Component
public class LocalAuthClient implements AuthClient {

    @Override
    public Long saveUser(UserSignupRequest request) {
        // TODO: User 도메인에서 제공해 주는 회원가입 API 사용
        return null;
    }
}
