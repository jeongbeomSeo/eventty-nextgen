package com.eventty.eventtynextgen.certification.response;

public record CertificationExistsResponseView(
    String email,
    boolean exists
) {
}
