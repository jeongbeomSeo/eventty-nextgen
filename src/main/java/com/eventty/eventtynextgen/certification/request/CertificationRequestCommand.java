package com.eventty.eventtynextgen.certification.request;

import com.eventty.eventtynextgen.user.shared.annotation.EmailRegexp;

public record CertificationRequestCommand(
    @EmailRegexp
    String email
) {

}
