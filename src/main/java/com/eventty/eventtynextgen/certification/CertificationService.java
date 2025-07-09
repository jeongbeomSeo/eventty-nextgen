package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationIssueCertificationTokenResponseView;
import jakarta.servlet.http.HttpServletResponse;

public interface CertificationService {

    CertificationIssueCertificationTokenResponseView issueCertificationToken(String appName, String appSecret, HttpServletResponse response);
}
