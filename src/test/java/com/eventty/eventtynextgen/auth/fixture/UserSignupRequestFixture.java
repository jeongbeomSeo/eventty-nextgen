package com.eventty.eventtynextgen.auth.fixture;

import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;

public class UserSignupRequestFixture {

    public static UserSignupRequest successUserSignupRequest() {
        return new UserSignupRequest(1L, "홍길동", "000-0000-0000", "1999-01-01");
    }

}
