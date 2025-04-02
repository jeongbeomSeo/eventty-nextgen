package com.eventty.eventtynextgen.certification.response;

public record CertificationValidateCodeResponseView(
    String code,
    boolean validate
) {
}
