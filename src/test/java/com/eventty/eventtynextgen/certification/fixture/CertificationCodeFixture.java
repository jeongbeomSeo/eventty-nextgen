package com.eventty.eventtynextgen.certification.fixture;

import com.eventty.eventtynextgen.certification.entity.CertificationCode;
import java.time.LocalDateTime;

public class CertificationCodeFixture {

    public static CertificationCode createBaseEntityFromDb(String email) {
        return new CertificationCode(1L, email, "RANDOM", LocalDateTime.now().plusMinutes(10));
    }

    public static CertificationCode createBaseEntity(String email, String code) {
        return new CertificationCode(1L, email, code, LocalDateTime.now().plusMinutes(10));
    }

    public static CertificationCode createExpiredEntity(String email, String code) {
        return new CertificationCode(1L, email, code, LocalDateTime.now().minusMinutes(10));
    }

}
