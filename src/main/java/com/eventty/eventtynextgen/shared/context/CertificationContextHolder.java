package com.eventty.eventtynextgen.shared.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CertificationContextHolder {

    public static final ThreadLocal<CertificationContext> contextHolder = new ThreadLocal<>();

    public static void clearContext() {
        contextHolder.remove();
    }

    public static CertificationContext getContext() {
        CertificationContext result = contextHolder.get();
        if (result == null) {
            CertificationContext context = createEmptyContext();
            contextHolder.set(context);
            result = context;
        }

        return result;
    }

    public static void setContext(CertificationContext context) {
        Assert.notNull(context, "Only non-null certificationContext instances are permitted");
        contextHolder.set(context);
    }

    public static CertificationContext createEmptyContext() {
        return new CertificationContext();
    }
}
