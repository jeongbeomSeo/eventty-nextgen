package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.authorization.AuthorizationProvider;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.JwtTokenProvider;
import com.eventty.eventtynextgen.certification.core.RefreshTokenProvider;
import com.eventty.eventtynextgen.certification.core.userdetails.LoginIdUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final AuthenticationProvider authenticationProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    @Override
    public CertificationLoginResult login(String loginId, String password) {

        Authentication authenticate = this.authenticationProvider.authenticate(
            LoginIdPasswordAuthenticationToken.unauthenticated(LoginIdUserDetails.fromCredentials(loginId, password)));

        String accessToken = this.jwtTokenProvider.createAccessToken(authenticate);

        String refreshToken = this.refreshTokenProvider.createRefreshToken(authenticate);

        return null;
    }
}
