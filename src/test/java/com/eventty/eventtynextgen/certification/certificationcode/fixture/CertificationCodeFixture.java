package com.eventty.eventtynextgen.certification.certificationcode.fixture;

import com.eventty.eventtynextgen.certification.certificationcode.entity.CertificationCode;

public class CertificationCodeFixture {

    public static CertificationCode createCertificationCode(String email, String code, int ttl) {
        return CertificationCode.of(email, code, ttl);
    }

}
