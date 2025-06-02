package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationIssueCertificationTokenResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationReissueAccessTokenResponseView;
import jakarta.servlet.http.HttpServletResponse;

public interface CertificationService {

    CertificationLoginResponseView login(String loginId, String password, HttpServletResponse response);

    void logout(Long userId, HttpServletResponse response);

    CertificationReissueAccessTokenResponseView reissueAccessToken(Long userId, String accessToken, String refreshToken, HttpServletResponse response);

    CertificationIssueCertificationTokenResponseView issueCertificationToken(String appName);
}
