package com.eventty.eventtynextgen.certification.response;

public record CertificationLoginResponseView(
    Long userId,
    String email,
    String name,
    TokenInfo tokenInfo
) {
    public record TokenInfo(
        String tokenType,
        String accessToken
    ){}

}
