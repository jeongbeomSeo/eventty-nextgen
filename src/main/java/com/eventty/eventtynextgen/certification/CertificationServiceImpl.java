package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties;
import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties.Permission;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.AccessTokenInfo;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.certification.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.authorization.AuthorizationProvider;
import com.eventty.eventtynextgen.certification.authuser.AuthUserService;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.userdetails.LoginIdUserDetails;
import com.eventty.eventtynextgen.certification.refreshtoken.RefreshTokenService;
import com.eventty.eventtynextgen.certification.refreshtoken.entity.RefreshToken;
import com.eventty.eventtynextgen.certification.response.CertificationIssueCertificationTokenResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationReissueAccessTokenResponseView;
import com.eventty.eventtynextgen.certification.shared.utils.CookieUtils;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
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
    private final RefreshTokenService refreshTokenService;
    private final UserComponent userComponent;
    private final AuthorizationApiProperties authorizationApiProperties;
    private final AuthUserService authUserService;

    @Override
    @Transactional
    public CertificationLoginResponseView login(String loginId, String password, HttpServletResponse response) {
        // 1. 사용자 검증
        Authentication authenticate = this.authenticationProvider.authenticate(
            LoginIdPasswordAuthenticationToken.unauthenticated(LoginIdUserDetails.fromCredentials(loginId, password)));

        // 2. 사용자 역할 확인 및 권한 할당
        Authentication authorizedAuthentication = this.authorizationProvider.authorize(authenticate);

        // 3. 토큰 생성
        AccessTokenInfo tokenInfo = this.jwtTokenProvider.createAccessToken(authorizedAuthentication);

        // 4. 토큰(Session ID)와 로그인한 사용자 정보 영속화
        authUserService.saveAuthUser(tokenInfo.getAccessToken(), tokenInfo.getAccessTokenExpiredAt(), authorizedAuthentication);

        // 5. Refresh Token 영속화
        this.refreshTokenService.saveOrUpdate(tokenInfo.getRefreshToken(), authorizedAuthentication.getUserDetails().getUserId());

        // 6. Refresh Token 헤더에 추가
        CookieUtils.addRefreshToken(tokenInfo.getRefreshToken(), response);

        return new CertificationLoginResponseView(
            authorizedAuthentication.getUserDetails().getUserId(),
            authorizedAuthentication.getUserDetails().getLoginId(),
            new CertificationLoginResponseView.AccessTokenInfo(tokenInfo.getTokenType(), tokenInfo.getAccessToken()));
    }

    @Override
    public void logout(Long userId, HttpServletResponse response) {
        this.refreshTokenService.delete(userId);

        CookieUtils.removeRefreshToken(response);
    }

    @Override
    @Transactional
    public CertificationReissueAccessTokenResponseView reissueAccessToken(Long userId, String accessToken, String refreshToken, HttpServletResponse response) {
        // 1. Refresh 토큰 검증
        this.verifyAndHandleTokenException(refreshToken);

        // 2. Refresh 토큰 값 일치 확인
        RefreshToken refreshTokenFromDb = this.refreshTokenService.getRefreshToken(userId);
        if (!Objects.equals(refreshTokenFromDb.getRefreshToken(), refreshToken)) {
            throw CustomException.badRequest(CertificationErrorType.MISMATCH_REFRESH_TOKEN);
        }

        // 3. 사용자가 존재하며 활성화 상태인지 확인
        this.userComponent.findByUserId(userId)
            .map(user -> {
                if (user.isDeleted()) {
                    throw CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED);
                }
                return user;
            })
            .orElseThrow(() -> CustomException.badRequest(UserErrorType.NOT_FOUND_USER));

        // 4. 토큰 재발급
        AccessTokenInfo tokenInfo = this.jwtTokenProvider.createAccessTokenByExpiredToken();

        // 5. 새로 발급한 Refresh Token 저장
        this.refreshTokenService.saveOrUpdate(tokenInfo.getRefreshToken(), userId);

        // 6. Refresh Token 헤더에 추가
        CookieUtils.addRefreshToken(tokenInfo.getRefreshToken(), response);

        return new CertificationReissueAccessTokenResponseView(
            userId,
            new CertificationReissueAccessTokenResponseView.AccessTokenInfo(tokenInfo.getTokenType(), tokenInfo.getAccessToken()));
    }

    private void verifyAndHandleTokenException(String token) {
        try {
            this.jwtTokenProvider.verifyToken(token);
        } catch (ExpiredJwtException ex) {
            throw CustomException.badRequest(CertificationErrorType.JWT_TOKEN_EXPIRED);
        } catch (Exception ex) {
            throw CustomException.badRequest(CertificationErrorType.FAILED_TOKEN_VERIFIED);
        }
    }

    @Override
    public CertificationIssueCertificationTokenResponseView issueCertificationToken(String appName) {
        // 1. 해당 appName properties에 존재하는지 확인
        if (!this.authorizationApiProperties.containsAppName(appName)) {
            throw CustomException.badRequest(CertificationErrorType.NOT_FOUND_APP_NAME);
        }

        // 2. 존재한다면, Token을 생성
        Map<String, Permission> apiPermissions = this.authorizationApiProperties.getPermissions(appName);
        CertificationTokenInfo certificationToken = this.jwtTokenProvider.createCertificationToken(appName, apiPermissions);

        // 3. Token을 반환
        return new CertificationIssueCertificationTokenResponseView(certificationToken);
    }
}
