package com.eventty.eventtynextgen.auth.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConst {

    public static final int EMAIL_VERIFICATION_CODE_LEN = 6; // 이메일 인증 코드 길이
    public static final int AUTH_CODE_TTL = 10;    // 인증 코드 생명 기간 (10분)
    public static final Long ACCESS_TOKEN_VALIDITY_IN_MIN = 120L * 60 * 1000; // 2시간
    public static final Long REFRESH_TOKEN_VALIDITY_IN_MIN = 10080L * 60 * 1000; // 1주일
}
