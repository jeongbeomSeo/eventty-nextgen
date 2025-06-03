package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.base.manager.AppAuthorizationManager;
import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties.Permission;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.LoginTokensInfo;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.certification.authuser.AuthUserService;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.refreshtoken.RefreshTokenService;
import com.eventty.eventtynextgen.certification.response.CertificationIssueCertificationTokenResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationReissueAccessTokenResponseView;
import com.eventty.eventtynextgen.certification.service.AuthService;
import com.eventty.eventtynextgen.certification.service.TokenService;
import com.eventty.eventtynextgen.certification.shared.utils.CookieUtils;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final AuthService authService;
    private final TokenService tokenService;
    private final AppAuthorizationManager appAuthorizationManager;
    private final AuthUserService authUserService;

    @Override
    @Transactional
    public CertificationLoginResponseView login(String loginId, String password, HttpServletResponse response) {
        // 1. 사용자 검증 & 권한 할당
        Authentication authenticate = authService.authenticateAndAuthorize(loginId, password);

        // 2. 토큰 생성
        LoginTokensInfo loginTokensInfo = this.tokenService.issueTokensAndSaveRefresh(authenticate);

        // 3. AuthUser 영속화
        this.authUserService.saveAuthUser(
            loginTokensInfo.getAccessToken(),
            loginTokensInfo.getAccessTokenExpiredAt(),
            authenticate.getUserDetails().getUserId(),
            this.authService.joinAuthorities(authenticate.getAuthorities()));

        // 4. Refresh Token 헤더에 추가
        CookieUtils.addRefreshToken(loginTokensInfo.getRefreshToken(), response);

        return new CertificationLoginResponseView(
            authenticate.getUserDetails().getUserId(),
            authenticate.getUserDetails().getLoginId(),
            new CertificationLoginResponseView.AccessTokenInfo(loginTokensInfo.getTokenType(), loginTokensInfo.getAccessToken()));
    }

    @Override
    public void logout(Long userId, HttpServletResponse response) {
        this.tokenService.deleteRefresh(userId);

        CookieUtils.removeRefreshToken(response);
    }

    @Override
    @Transactional
    public CertificationReissueAccessTokenResponseView reissueAccessToken(Long userId, String accessToken, String refreshToken, HttpServletResponse response) {
        // 1. Refresh 토큰 검증 & 토큰 일치 여부 확인
        this.tokenService.verifyAndMatchRefresh(refreshToken, userId);

        // 2. 사용자가 존재하며 활성화 상태인지 확인
        User user = this.authService.getActivatedUser(userId);

        // 3. 토큰 재발급
        LoginTokensInfo loginTokensInfo = this.tokenService.reissueTokensAndSaveRefresh(user.getId());

        // 4. AuthUser 영속화
        authUserService.saveAuthUser(loginTokensInfo.getAccessToken(), loginTokensInfo.getAccessTokenExpiredAt(),
            user.getId(), this.authService.getJoinedAuthoritiesByUserRole(user.getUserRole()));

        // 5. Refresh Token 헤더에 추가
        CookieUtils.addRefreshToken(loginTokensInfo.getRefreshToken(), response);

        return new CertificationReissueAccessTokenResponseView(
            userId,
            new CertificationReissueAccessTokenResponseView.AccessTokenInfo(loginTokensInfo.getTokenType(), loginTokensInfo.getAccessToken()));
    }

    @Override
    public CertificationIssueCertificationTokenResponseView issueCertificationToken(String appName) {
        // 1. 해당 appName properties에 존재하는지 확인
        if (!this.appAuthorizationManager.containsAppName(appName)) {
            throw CustomException.badRequest(CertificationErrorType.NOT_FOUND_APP_NAME);
        }

        // 2. API Allow 리스트를 조회
        Map<String, Permission> apiPermissions = this.appAuthorizationManager.getPermissions(appName);

        // 3. Certification Token을 발급
        CertificationTokenInfo certificationToken = this.tokenService.issueCertificationToken(appName, apiPermissions);

        return new CertificationIssueCertificationTokenResponseView(certificationToken);
    }
}
