package com.eventty.eventtynextgen.shared.context;

import java.util.Objects;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class AuthorizationContext {

    private Long userId;
    private String role;

    public AuthorizationContext() {
    }

    public void updateContext(Long userId, String role) {
        this.userId = userId;
        this.role = role;
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
