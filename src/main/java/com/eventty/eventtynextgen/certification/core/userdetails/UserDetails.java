package com.eventty.eventtynextgen.certification.core.userdetails;

public interface UserDetails {

    Long getUserId();

    String getLoginId();

    String getPassword();

    boolean isIdentified();

    default boolean isActive() {
        return true;
    }
}
