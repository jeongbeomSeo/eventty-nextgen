package com.eventty.eventtynextgen.certification.service;

import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties.Permission;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenInfo;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.LoginTokensInfo;
import com.eventty.eventtynextgen.certification.core.Authentication;
import java.util.Map;

public interface TokenService {

    LoginTokensInfo issueTokensAndSaveRefresh(Authentication authentication);

    LoginTokensInfo reissueTokensAndSaveRefresh(Long userId);

    void deleteRefresh(Long userId);

    void verifyAndMatchRefresh(String refreshToken, Long userId);

    CertificationTokenInfo issueCertificationToken(String appName, Map<String, Permission> apiPermissions);
}
