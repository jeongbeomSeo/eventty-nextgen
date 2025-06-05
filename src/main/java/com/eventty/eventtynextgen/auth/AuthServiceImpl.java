package com.eventty.eventtynextgen.auth;

import com.eventty.eventtynextgen.auth.core.Authentication;
import com.eventty.eventtynextgen.auth.response.AuthLoginResponseView;
import com.eventty.eventtynextgen.auth.response.AuthReissueSessionTokenResponseView;
import com.eventty.eventtynextgen.auth.service.AuthUserService;
import com.eventty.eventtynextgen.auth.service.SessionTokenService;
import com.eventty.eventtynextgen.auth.shared.utils.CookieUtils;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.SessionTokenInfo;
import com.eventty.eventtynextgen.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserService authUserService;
    private final SessionTokenService sessionTokenService;

    @Override
    @Transactional
    public AuthLoginResponseView login(String loginId, String password, HttpServletResponse response) {
        // 1. 사용자 검증 & 권한 할당
        Authentication authenticate = authUserService.authenticateAndAuthorize(loginId, password);

        // 2. 토큰 생성
        SessionTokenInfo sessionTokenInfo = this.sessionTokenService.issueTokenAndSaveRefresh(authenticate);

        // 4. Refresh Token 헤더에 추가
        CookieUtils.addRefreshToken(sessionTokenInfo.getRefreshToken(), response);

        return new AuthLoginResponseView(
            authenticate.getUserDetails().getUserId(),
            authenticate.getUserDetails().getLoginId(),
            new AuthLoginResponseView.AccessTokenInfo(sessionTokenInfo.getTokenType(), sessionTokenInfo.getAccessToken()));
    }

    @Override
    public void logout(Long userId, HttpServletResponse response) {
        this.sessionTokenService.deleteRefresh(userId);

        CookieUtils.removeRefreshToken(response);
    }

    @Override
    @Transactional
    public AuthReissueSessionTokenResponseView reissueSessionToken(Long userId, String accessToken, String refreshToken, HttpServletResponse response) {
        // 1. Refresh 토큰 검증 & 토큰 일치 여부 확인
        this.sessionTokenService.verifyAndMatchRefresh(refreshToken, userId);

        // 2. 사용자가 존재하며 활성화 상태인지 확인
        User user = this.authUserService.getActivatedUser(userId);

        // 3. 토큰 재발급
        SessionTokenInfo loginTokensInfo = this.sessionTokenService.reissueTokenAndSaveRefresh(user.getId());

        // 5. Refresh Token 헤더에 추가
        CookieUtils.addRefreshToken(loginTokensInfo.getRefreshToken(), response);

        return new AuthReissueSessionTokenResponseView(
            userId,
            new AuthReissueSessionTokenResponseView.AccessTokenInfo(loginTokensInfo.getTokenType(), loginTokensInfo.getAccessToken()));
    }
}
