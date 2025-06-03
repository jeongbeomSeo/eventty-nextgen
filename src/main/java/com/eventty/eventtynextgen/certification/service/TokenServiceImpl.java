package com.eventty.eventtynextgen.certification.service;

import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties.Permission;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.LoginTokensInfo;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.refreshtoken.RefreshTokenService;
import com.eventty.eventtynextgen.certification.refreshtoken.entity.RefreshToken;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.CommonErrorType;
import com.eventty.eventtynextgen.shared.exception.enums.JwtTokenErrorType;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginTokensInfo issueTokensAndSaveRefresh(Authentication authentication) {
        if (!authentication.isAuthorized()) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorType.INVALID_INPUT_DATA,
                "Login Token의 발행은 인증·인가에 성공한 Authentcation 객체만 호출할 수 있습니다.");
        }

        LoginTokensInfo loginTokens = this.jwtTokenProvider.createLoginTokens();

        this.refreshTokenService.saveOrUpdate(loginTokens.getRefreshToken(), authentication.getUserDetails().getUserId());

        return loginTokens;
    }

    @Override
    public LoginTokensInfo reissueTokensAndSaveRefresh(Long userId) {

        LoginTokensInfo loginTokens = this.jwtTokenProvider.createLoginTokens();

        this.refreshTokenService.saveOrUpdate(loginTokens.getRefreshToken(), userId);

        return loginTokens;
    }

    @Override
    public void deleteRefresh(Long userId) {
        this.refreshTokenService.delete(userId);
    }

    @Override
    public void verifyAndMatchRefresh(String refreshToken, Long userId) {
        // 1. Refresh 토큰 검증
        this.verifyAndHandleTokenException(refreshToken);

        // 2. Refresh 토큰 값 일치 확인
        RefreshToken refreshTokenFromDb = this.refreshTokenService.getRefreshToken(userId);
        if (!Objects.equals(refreshTokenFromDb.getRefreshToken(), refreshToken)) {
            throw CustomException.badRequest(CertificationErrorType.MISMATCH_REFRESH_TOKEN);
        }
    }

    private void verifyAndHandleTokenException(String token) {
        switch (this.jwtTokenProvider.verifyToken(token)) {
            case EXPIRED_TOKEN -> throw CustomException.badRequest(JwtTokenErrorType.EXPIRED_TOKEN);
            case UNSUPPORTED_TOKEN -> throw CustomException.badRequest(JwtTokenErrorType.UNSUPPORTED_TOKEN);
            case ILLEGAL_STATE_TOKEN -> throw CustomException.badRequest(JwtTokenErrorType.ILLEGAL_STATE_TOKEN);
            case INVALID_SIGNATURE_TOKEN -> throw CustomException.badRequest(JwtTokenErrorType.FAILED_SIGNATURE_VALIDATION);
            case UNKNOWN_ERROR -> throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, JwtTokenErrorType.UNKNOWN_VERIFY_ERROR);
        }
    }

    @Override
    public CertificationTokenInfo issueCertificationToken(String appName, Map<String, Permission> apiPermissions) {
        return this.jwtTokenProvider.createCertificationToken(appName, apiPermissions);
    }
}
