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

    // TODO: Role + 사용자 정보 중 필요한 필드 추가 예정
    public void updateContext(Long userId) {
        this.userId = userId;
    }

    public boolean validate() {
        return Objects.nonNull(this.userId) && StringUtils.hasText(role);
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
