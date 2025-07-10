package com.eventty.eventtynextgen.shared.context;

import java.util.Objects;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class SessionContext {

    private Long userId;
    private String role;
    private boolean skipSessionCheck;

    public SessionContext() {}

    public void updateSessionInfo(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public boolean validate() {
        return Objects.nonNull(this.userId) && StringUtils.hasText(role);
    }

    public void markSessionCheckAsSkipped() {
        this.skipSessionCheck = true;
    }
}
