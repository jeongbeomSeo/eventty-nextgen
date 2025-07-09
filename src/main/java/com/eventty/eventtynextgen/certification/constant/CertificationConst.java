package com.eventty.eventtynextgen.certification.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CertificationConst {

    public static final Long CERTIFICATION_TOKEN_VALIDITY_IN_MS = 120L * 60 * 1000; // 2시간

    public static final String CERTIFICATION_TOKEN_COOKIE_NAME = "certification";
}
