package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationIssueTokenResponseView;
import jakarta.servlet.http.HttpServletResponse;

public interface CertificationService {

    CertificationIssueTokenResponseView issueCertificationToken(String appName, String appSecret, HttpServletResponse response);
}
