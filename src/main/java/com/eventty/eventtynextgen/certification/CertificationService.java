package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView.AccessTokenInfo;
import jakarta.servlet.http.HttpServletResponse;

public interface CertificationService {

    CertificationLoginResult login(String loginId, String password);

    void logout(Long userId, HttpServletResponse response);

    record CertificationLoginResult(
        Long userId,
        String loginId,
        TokenInfo tokenInfo
    ) {
        record TokenInfo(
            String accessTokenType,
            String accessToken,
            String refreshToken
        ) {}

        public CertificationLoginResponseView toCertificationLoginResponseView() {
            return new CertificationLoginResponseView(this.userId(), this.loginId(),
                new AccessTokenInfo(this.tokenInfo().accessTokenType(), this.tokenInfo().accessToken()));
        }
    }
}
