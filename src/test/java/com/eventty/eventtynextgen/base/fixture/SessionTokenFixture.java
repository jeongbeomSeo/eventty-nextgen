package com.eventty.eventtynextgen.base.fixture;

import static com.eventty.eventtynextgen.base.constant.BaseConst.*;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.SessionTokenInfo;

public class SessionTokenFixture {

    public static SessionTokenInfo createSessionToken(Long userId) {
        return JwtTokenProvider.createSessionToken(userId, 60 * 60 * 1000L, 60 * 60 * 1000L);
    }

    public static String createAccessTokenHeaderValue(Long userId) {
        return JWT_TOKEN_TYPE + " " + createAccessToken(userId);
    }

    public static String createAccessToken(Long userId) {
        return JwtTokenProvider.createSessionToken(userId, 60 * 60 * 1000L, 60 * 60 * 1000L).getAccessToken();
    }

    public static SessionTokenInfo createExpiredAccessTokenAndValidRefreshToken(Long userId) {
        return JwtTokenProvider.createSessionToken(userId, -60 * 60 * 1000L, 60 * 60 * 1000L);
    }

    public static SessionTokenInfo createExpiredAccessTokenAndExpiredRefreshToken(Long userId) {
        return JwtTokenProvider.createSessionToken(userId, -60 * 60 * 1000L, -60 * 60 * 1000L);
    }
}
