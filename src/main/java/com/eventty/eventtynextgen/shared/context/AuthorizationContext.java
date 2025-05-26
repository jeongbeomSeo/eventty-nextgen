package com.eventty.eventtynextgen.shared.context;

import java.util.Objects;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class AuthorizationContext {

    private Long userId;
    private String role;
    private String appName;

    public AuthorizationContext() {
    }

    public void updateContext(Long userId, String role, String appName) {
        this.userId = userId;
        this.role = role;
        this.appName = appName;
    }

    public boolean validate() {
        return this.userId != null && StringUtils.hasText(role);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AuthorizationContext that = (AuthorizationContext) object;
        return Objects.equals(userId, that.userId) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, role);
    }
}
