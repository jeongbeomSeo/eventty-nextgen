package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.SessionTokenInfo;
import com.eventty.eventtynextgen.auth.core.Authentication;

public interface SessionTokenService {

    SessionTokenInfo issueTokenAndSaveRefresh(Authentication authentication);

    SessionTokenInfo reissueTokenAndSaveRefresh(Long userId);

    void deleteRefresh(Long userId);

    void verifyAndMatchRefresh(String refreshToken, Long userId);

    Long getUserIdFromExpiredAccess(String accessToken);
}
