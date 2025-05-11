package com.eventty.eventtynextgen.certification.certificationcode;

import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationValidateCodeResponseView;

public interface CertificationCodeService {

    CertificationExistsResponseView checkExists(String certTarget);

    CertificationSendCodeResponseView sendCode(String certTarget);

    CertificationValidateCodeResponseView validateCode(String email, String code);
}
