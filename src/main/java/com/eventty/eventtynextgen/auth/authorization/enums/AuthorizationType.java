package com.eventty.eventtynextgen.auth.authorization.enums;

import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;

public enum AuthorizationType {
    ROLE_ADMIN,
    ROLE_HOST,
    ROLE_USER,
    NONE;

    /**
     * 주어진 사용자 역할 문자열로부터 {@code AuthorizationType}을 반환합니다.
     * 주어진 문자열이 "ROLE_" 접두사를 포함하지 않으면 자동으로 붙여서 비교합니다.
     * 일치하는 값이 없으면 {@link #NONE}을 반환합니다.
     *
     * @param userRoleType 사용자 역할 (예: "USER", "HOST", "ADMIN" 또는 "ROLE_USER", "ROLE_HOST" 등)
     * @return 대응하는 {@code AuthorizationType}, 없으면 {@link #NONE}
     */
    public static AuthorizationType from(UserRoleType userRoleType) {
        if (userRoleType == null) {
            return NONE;
        }

        String candidate = userRoleType.name().trim().toUpperCase();
        if (!candidate.startsWith("ROLE_")) {
            candidate = "ROLE_" + candidate;
        }

        try {
            return AuthorizationType.valueOf(candidate);
        } catch (IllegalArgumentException ex) {
            return NONE;
        }
    }
}