package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationIssueCertificationTokenResponseView;

public interface CertificationService {

    CertificationIssueCertificationTokenResponseView issueCertificationToken(String appName);
}
