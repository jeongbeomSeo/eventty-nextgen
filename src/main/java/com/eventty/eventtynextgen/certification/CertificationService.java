package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView.AccessTokenInfo;
import com.eventty.eventtynextgen.certification.response.CertificationReissueResponseView;
import jakarta.servlet.http.HttpServletResponse;

public interface CertificationService {

    CertificationLoginResponseView login(String loginId, String password, HttpServletResponse response);

    void logout(Long userId, HttpServletResponse response);

    CertificationReissueResponseView reissue(Long userId, String accessToken, String refreshToken, HttpServletResponse response);
}
