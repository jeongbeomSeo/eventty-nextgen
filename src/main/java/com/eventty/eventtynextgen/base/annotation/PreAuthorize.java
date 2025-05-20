package com.eventty.eventtynextgen.base.annotation;

import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreAuthorize {

    AuthorizationType[] value();
}
