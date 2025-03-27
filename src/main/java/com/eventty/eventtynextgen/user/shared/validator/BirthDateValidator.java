package com.eventty.eventtynextgen.user.shared.validator;

import com.eventty.eventtynextgen.user.shared.annotation.ValidBirthDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, String> {

    private static final DateTimeFormatter DOT_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DASH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(ValidBirthDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return false; // null 값은 허용하지 않음
        }

        return isValidFormat(dateOfBirth, DOT_FORMATTER) || isValidFormat(dateOfBirth, DASH_FORMATTER);
    }

    private boolean isValidFormat(String dateStr, DateTimeFormatter formatter) {
        try {
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
