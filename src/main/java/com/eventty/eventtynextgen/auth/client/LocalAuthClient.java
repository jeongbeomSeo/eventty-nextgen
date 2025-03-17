package com.eventty.eventtynextgen.auth.client;

import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import org.springframework.stereotype.Component;

@Component
public class LocalAuthClient implements AuthClient {

    @Override
    public Long saveUser(AuthUser authUser) {
        // TODO: User 도메인에서 제공해 주는 회원가입 API 사용
        return null;
    }
}
