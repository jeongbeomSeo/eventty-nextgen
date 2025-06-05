package com.eventty.eventtynextgen.auth.core.userdetails;

import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;

public class LoginIdUserDetails implements UserDetails {

    private final Long userId;
    private final String loginId;
    private final String password;
    private final UserRoleType userRole;
    private final boolean isDeleted;

    private LoginIdUserDetails(Long userId, String loginId, String password, UserRoleType userRole, boolean isDeleted) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
        this.userRole = userRole;
        this.isDeleted = isDeleted;
    }

    public static UserDetails fromCredentials(String loginId, String password) {
        return new LoginIdUserDetails(null, loginId, password, null, false);
    }

    public static UserDetails fromPrincipal(Long userId, String loginId, String password, UserRoleType userRole, boolean isDeleted) {
        return new LoginIdUserDetails(userId, loginId, password, userRole, isDeleted);
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
    public UserRoleType getUserRole() {
        return this.userRole;
    }

    @Override
    public boolean isIdentified() {
        return this.userId != null;
    }

    @Override
    public boolean isActive() {
        return !this.isDeleted;
    }
}
