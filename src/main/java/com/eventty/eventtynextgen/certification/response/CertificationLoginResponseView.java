package com.eventty.eventtynextgen.certification.response;

public record CertificationLoginResponseView(
    Long userId,
    String email,
    String name,
    AccessTokenInfo accessTokenInfo
) {
    public record AccessTokenInfo(
        String tokenType,
        String accessToken
    ){}
}
