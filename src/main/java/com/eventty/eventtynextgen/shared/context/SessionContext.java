package com.eventty.eventtynextgen.shared.context;

import static com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.*;

import java.util.Objects;
import lombok.Getter;

@Getter
public class SessionContext {

    private Long userId;
    private String role;
    private String sessionToken;
    private VerifyTokenResult verifyTokenResult;

    public SessionContext() {}

    public void updateSessionInfo(Long userId, String sessionToken, VerifyTokenResult verifyTokenResult) {
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.verifyTokenResult = verifyTokenResult;
    }

    public void updateSessionInfo(String sessionToken, VerifyTokenResult verifyTokenResult) {
        this.sessionToken = sessionToken;
        this.verifyTokenResult = verifyTokenResult;
    }

    public void updateRole(String role) {
        this.role = role;
    }

    public boolean isSuccessParsingToken() {
        return this.getVerifyTokenResult() == VerifyTokenResult.VERIFIED_TOKEN;
    }
}
