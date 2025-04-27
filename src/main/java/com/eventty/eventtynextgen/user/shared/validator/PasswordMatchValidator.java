package com.eventty.eventtynextgen.user.shared.validator;

import com.eventty.eventtynextgen.user.request.UserChangePasswordRequestCommand;
import com.eventty.eventtynextgen.user.shared.annotation.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserChangePasswordRequestCommand> {

    @Override
    public boolean isValid(
        UserChangePasswordRequestCommand cmd,
        ConstraintValidatorContext context) {

        if (cmd == null) return false;
        if (cmd.updatedPassword().equals(cmd.updatedPasswordConfirm())) {
            return true;
        }

        // 기본 글로벌 에러 억제
        context.disableDefaultConstraintViolation();
        // 'updatedPasswordConfirm' 필드에 에러를 붙여서 FieldError 로 만들어 준다
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
            .addPropertyNode("updatedPasswordConfirm")
            .addConstraintViolation();

        return false;
    }

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
