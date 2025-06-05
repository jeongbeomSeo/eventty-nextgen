package com.eventty.eventtynextgen.auth.core.userdetails;

import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;

public interface UserDetails {

    Long getUserId();

    String getLoginId();

    String getPassword();

    UserRoleType getUserRole();

    boolean isIdentified();

    default boolean isActive() {
        return true;
    }
}
