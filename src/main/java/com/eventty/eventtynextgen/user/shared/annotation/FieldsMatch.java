package com.eventty.eventtynextgen.user.shared.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsMatch {

    /**
     * 대상 필드 이름
     */
    String base();

    /**
     * 확인용 필드 이름
     */
    String match();
    String message() default "값이 일치하지 않습니다.";
}
