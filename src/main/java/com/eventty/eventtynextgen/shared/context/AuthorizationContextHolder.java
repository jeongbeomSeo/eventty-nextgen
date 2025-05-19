package com.eventty.eventtynextgen.shared.context;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

@UtilityClass
public class AuthorizationContextHolder {

    private static final ThreadLocal<AuthorizationContext> contextHolder = new ThreadLocal<>();

    public static void clearContext() {
        contextHolder.remove();
    }

    public static AuthorizationContext getContext() {
        AuthorizationContext result = contextHolder.get();
        if (result == null) {
            AuthorizationContext context = createEmptyContext();
            contextHolder.set(context);
            result = context;
        }

        return result;
    }

    public static void setContext(AuthorizationContext context) {
        Assert.notNull(context, "Only non-null authorizationContext instances are permitted");
        contextHolder.set(context);
    }

    private static AuthorizationContext createEmptyContext() {
        return new AuthorizationContext();
    }

    // TODO - DefferedContext 기능 추가 (비동기 처리 기능이 추가되면서 컨텍스트 전파에서 필요할 가능성이 있음)
}
