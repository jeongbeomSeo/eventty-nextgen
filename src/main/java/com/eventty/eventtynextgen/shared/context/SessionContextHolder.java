package com.eventty.eventtynextgen.shared.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionContextHolder {

    private static final ThreadLocal<SessionContext> contextHolder = new ThreadLocal<>();

    public static void clearContext() {
        contextHolder.remove();
    }

    public static SessionContext getContext() {
        SessionContext result = contextHolder.get();
        if (result == null) {
            SessionContext context = createEmptyContext();
            contextHolder.set(context);
            result = context;
        }

        return result;
    }

    public static void setContext(SessionContext context) {
        Assert.notNull(context, "Only non-null sessionContext instances are permitted");
        contextHolder.set(context);
    }

    private static SessionContext createEmptyContext() {
        return new SessionContext();
    }
}
