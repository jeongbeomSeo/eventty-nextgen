package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.authorization.AuthorizationProvider;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.TokenInfo;
import com.eventty.eventtynextgen.certification.core.userdetails.LoginIdUserDetails;
import com.eventty.eventtynextgen.certification.refreshtoken.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final AuthenticationProvider authenticationProvider;
    private final AuthorizationProvider authorizationProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public CertificationLoginResult login(String loginId, String password) {

        Authentication authenticate = this.authenticationProvider.authenticate(
            LoginIdPasswordAuthenticationToken.unauthenticated(LoginIdUserDetails.fromCredentials(loginId, password)));

        Authentication authorizedAuthentication = this.authorizationProvider.authorize(authenticate);

        TokenInfo tokenInfo = this.jwtTokenProvider.createToken(authorizedAuthentication);

        refreshTokenService.saveOrUpdate(tokenInfo.getRefreshToken(), authorizedAuthentication.getUserDetails().getUserId());

        return new CertificationLoginResult(authorizedAuthentication.getUserDetails().getUserId(), authorizedAuthentication.getUserDetails().getLoginId(),
            new CertificationLoginResult.TokenInfo(tokenInfo.getTokenType(), tokenInfo.getAccessToken(), tokenInfo.getRefreshToken()));
    }

    @Override
    public void logout(Long userId, HttpServletResponse response) {

        refreshTokenService.delete(userId);

        ResponseCookie removedRefreshTokenCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .domain("localhost")
            .maxAge(0)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, removedRefreshTokenCookie.toString());
    }
}
