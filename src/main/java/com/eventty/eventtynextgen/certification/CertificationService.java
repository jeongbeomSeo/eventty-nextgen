package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView.AccessTokenInfo;

public interface CertificationService {

    CertificationLoginResult login(String loginId, String password);

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
