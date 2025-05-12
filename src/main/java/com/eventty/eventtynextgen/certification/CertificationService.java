package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView.AccessTokenInfo;

public interface CertificationService {

    CertificationLoginResult login(String loginId, String password);

    record CertificationLoginResult(
        Long userId,
        String email,
        String name,
        TokenInfo tokenInfo
    ) {
        record TokenInfo(
            String accessTokenType,
            String accessToken,
            String refreshToken
        ) {}

        public CertificationLoginResponseView toCertificationLoginResponseView() {
            return new CertificationLoginResponseView(this.userId(), this.email(), this.name(),
                new AccessTokenInfo(this.tokenInfo().accessTokenType(), this.tokenInfo().accessToken()));
        }
    }
}
