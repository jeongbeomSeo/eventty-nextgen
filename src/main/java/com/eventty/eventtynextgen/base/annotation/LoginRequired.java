package com.eventty.eventtynextgen.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

    boolean requireLogin() default true;

    boolean requireAdmin() default false;

    boolean requireHost() default false;

    boolean requireUser() default false;
}
