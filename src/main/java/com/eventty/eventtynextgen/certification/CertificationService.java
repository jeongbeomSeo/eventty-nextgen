package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationValidateCodeResponseView;

public interface CertificationService {

    CertificationExistsResponseView checkExists(String certTarget);

    CertificationSendCodeResponseView sendCode(String certTarget);

    CertificationValidateCodeResponseView validateCode(String code);
}
