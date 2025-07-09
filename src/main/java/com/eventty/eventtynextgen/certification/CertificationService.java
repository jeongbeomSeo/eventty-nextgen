package com.eventty.eventtynextgen.certification;

import jakarta.servlet.http.HttpServletResponse;

public interface CertificationService {

    void issueCertificationToken(String appName, String appSecret, HttpServletResponse response);
}
