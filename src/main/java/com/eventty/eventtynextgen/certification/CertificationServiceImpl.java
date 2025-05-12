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
    private final AuthorizationProvider authorizationProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    @Override
    public CertificationLoginResult login(String loginId, String password) {

        Authentication authenticate = this.authenticationProvider.authenticate(
            LoginIdPasswordAuthenticationToken.unauthenticated(LoginIdUserDetails.fromCredentials(loginId, password)));

        Authentication grantedAuthenticate = this.authorizationProvider.authorize(authenticate);

        String accessToken = this.jwtTokenProvider.createAccessToken(grantedAuthenticate);

        String refreshToken = this.refreshTokenProvider.createRefreshToken(grantedAuthenticate);

        return null;
    }
}
