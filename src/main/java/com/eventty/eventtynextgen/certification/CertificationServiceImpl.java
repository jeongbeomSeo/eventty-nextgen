package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.TokenInfo;
import com.eventty.eventtynextgen.certification.authentication.AuthenticationProvider;
import com.eventty.eventtynextgen.certification.authentication.LoginIdPasswordAuthenticationToken;
import com.eventty.eventtynextgen.certification.authorization.AuthorizationProvider;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.userdetails.LoginIdUserDetails;
import com.eventty.eventtynextgen.certification.refreshtoken.RefreshTokenService;
import com.eventty.eventtynextgen.certification.refreshtoken.entity.RefreshToken;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationReissueResponseView;
import com.eventty.eventtynextgen.certification.shared.utils.CookieUtils;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    @Transactional
    public CertificationLoginResponseView login(String loginId, String password, HttpServletResponse response) {
        // 1. 사용자 검증
        Authentication authenticate = this.authenticationProvider.authenticate(
            LoginIdPasswordAuthenticationToken.unauthenticated(LoginIdUserDetails.fromCredentials(loginId, password)));

        // 2. 사용자 역할 확인 및 권한 할당
        Authentication authorizedAuthentication = this.authorizationProvider.authorize(authenticate);

        // 3. 토큰 생성
        TokenInfo tokenInfo = this.jwtTokenProvider.createToken(authorizedAuthentication);

        // 4. Refresh Token 저장
        this.refreshTokenService.saveOrUpdate(tokenInfo.getRefreshToken(), authorizedAuthentication.getUserDetails().getUserId());

        // 5. Refresh Token 헤더에 추가
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
    public CertificationReissueResponseView reissue(Long userId, String accessToken, String refreshToken, HttpServletResponse response) {
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
        TokenInfo tokenInfo = this.jwtTokenProvider.createTokenByExpiredToken(accessToken);

        // 5. 새로 발급한 Refresh Token 저장
        this.refreshTokenService.saveOrUpdate(tokenInfo.getRefreshToken(), userId);

        // 6. Refresh Token 헤더에 추가
        CookieUtils.addRefreshToken(tokenInfo.getRefreshToken(), response);

        return new CertificationReissueResponseView(
            userId,
            new CertificationReissueResponseView.AccessTokenInfo(tokenInfo.getTokenType(), tokenInfo.getAccessToken()));
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
}
