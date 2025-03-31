package com.eventty.eventtynextgen.certification.request;

import com.eventty.eventtynextgen.user.shared.annotation.EmailRegexp;
import jakarta.validation.constraints.Email;

public record CertificationExistsRequestCommand(
    @EmailRegexp
    String email
) {

}
