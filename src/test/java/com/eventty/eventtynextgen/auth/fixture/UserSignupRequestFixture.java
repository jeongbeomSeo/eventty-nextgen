package com.eventty.eventtynextgen.auth.fixture;

import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;

public class UserSignupRequestFixture {

    public static UserSignupRequest successUserSignupRequest() {
        return new UserSignupRequest(1L, "홍길동", "000-0000-0000", "1999-01-01");
    }

    public static UserSignupRequest invalidAuthUserIdRequest() {
        return new UserSignupRequest(null, "홍길동", "000-0000-0000", "1999-01-01");
    }

    public static UserSignupRequest invalidNameRequest() {
        return new UserSignupRequest(1L, null, "000-0000-0000", "1999-01-01");
    }

    public static UserSignupRequest invalidPhoneNumberRequest() {
        return new UserSignupRequest(1L, "홍길동", "00000000000", "1999-01-01");
    }

    public static UserSignupRequest invalidBirthdateFormatRequest() {
        return new UserSignupRequest(1L, "홍길동", "000-0000-0000", "19990101");
    }
}
