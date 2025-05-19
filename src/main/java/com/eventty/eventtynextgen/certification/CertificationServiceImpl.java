package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.authorization.AuthorizationProvider;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.TokenInfo;
import com.eventty.eventtynextgen.certification.core.userdetails.LoginIdUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final AuthenticationProvider authenticationProvider;
    private final AuthorizationProvider authorizationProvider;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public CertificationLoginResult login(String loginId, String password) {

        Authentication authenticate = this.authenticationProvider.authenticate(
            LoginIdPasswordAuthenticationToken.unauthenticated(LoginIdUserDetails.fromCredentials(loginId, password)));

        Authentication authorizedAuthentication = this.authorizationProvider.authorize(authenticate);

        TokenInfo tokenInfo = this.jwtTokenProvider.createToken(authorizedAuthentication);

        return new CertificationLoginResult(authenticate.getUserDetails().getUserId(), authenticate.getUserDetails().getLoginId(),
            new CertificationLoginResult.TokenInfo(tokenInfo.getTokenType(), tokenInfo.getAccessToken(), tokenInfo.getRefreshToken()));
    }
}
