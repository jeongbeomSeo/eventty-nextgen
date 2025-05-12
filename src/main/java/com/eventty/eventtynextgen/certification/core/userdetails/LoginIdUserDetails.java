package com.eventty.eventtynextgen.certification.core.userdetails;

public class LoginIdUserDetails implements UserDetails {

    private final Long userId;
    private final String loginId;
    private final String password;
    private final boolean isDeleted;
    private final UserState userState;

    private LoginIdUserDetails(Long userId, String loginId, String password, boolean isDeleted, UserState userState) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
        this.isDeleted = isDeleted;
        this.userState = userState;
    }

    public static LoginIdUserDetails fromCredentials(String loginId, String password) {
        return new LoginIdUserDetails(null, loginId, password, false, UserState.UNIDENTIFIED);
    }

    public static LoginIdUserDetails fromPrincipal(Long userId, String loginId, String password, boolean isDeleted) {
        return new LoginIdUserDetails(userId, loginId, password, isDeleted, UserState.IDENTIFIED);
    }

    @Override
    public Long getUserId() {
        return this.userId;
    }

    @Override
    public String getLoginId() {
        return this.loginId;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isIdentified() {
        return this.userState == UserState.IDENTIFIED;
    }

    @Override
    public boolean isActive() {
        return !this.isDeleted;
    }

    enum UserState {
        IDENTIFIED,
        UNIDENTIFIED
    }
}
