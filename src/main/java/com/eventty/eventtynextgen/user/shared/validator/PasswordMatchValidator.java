package com.eventty.eventtynextgen.user.shared.validator;

import com.eventty.eventtynextgen.user.request.UserChangePasswordRequestCommand;
import com.eventty.eventtynextgen.user.shared.annotation.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserChangePasswordRequestCommand> {

    @Override
    public boolean isValid(UserChangePasswordRequestCommand command, ConstraintValidatorContext context) {
        if (command == null) {
            return false;
        }
        return command.updatedPassword().equals(command.updatedPasswordConfirm());
    }

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
