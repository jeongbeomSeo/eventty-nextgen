package com.eventty.eventtynextgen.certification.fixture;

import com.eventty.eventtynextgen.certification.entity.CertificationCode;

public class CertificationCodeFixture {

    public static CertificationCode createCertificationCode(String email, String code) {
        return CertificationCode.of(email, code);
    }

}
