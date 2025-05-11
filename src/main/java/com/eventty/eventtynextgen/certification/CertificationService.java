package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;

public interface CertificationService {

    CertificationLoginResponseView login(String loginId, String password);
}
