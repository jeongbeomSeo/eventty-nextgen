package com.eventty.eventtynextgen.certification.request;

import com.eventty.eventtynextgen.user.shared.annotation.EmailRegexp;

public record CertificationSendCodeRequestCommand(
    @EmailRegexp
    String email
) {

}
