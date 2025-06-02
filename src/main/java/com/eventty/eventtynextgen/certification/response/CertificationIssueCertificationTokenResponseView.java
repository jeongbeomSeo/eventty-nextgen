package com.eventty.eventtynextgen.certification.response;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenInfo;

public record CertificationIssueCertificationTokenResponseView(
    CertificationTokenInfo certificationToken
) {
}
