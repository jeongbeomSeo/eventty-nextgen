package com.eventty.eventtynextgen.certification.request;

public record CertificationLoginRequestCommand(
    String loginId,
    String password
){
}
