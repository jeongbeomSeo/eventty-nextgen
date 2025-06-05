package com.eventty.eventtynextgen.auth;

import com.eventty.eventtynextgen.auth.response.AuthLoginResponseView;
import com.eventty.eventtynextgen.auth.response.AuthReissueSessionTokenResponseView;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthLoginResponseView login(String loginId, String password, HttpServletResponse response);

    void logout(Long userId, HttpServletResponse response);

    AuthReissueSessionTokenResponseView reissueSessionToken(Long userId, String accessToken, String refreshToken, HttpServletResponse response);
}
