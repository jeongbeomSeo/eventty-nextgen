package com.eventty.eventtynextgen.user.shared.annotation;

import com.eventty.eventtynextgen.user.shared.validator.BirthDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BirthDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBirthDate {
    String message() default "Invalid date of birth format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
