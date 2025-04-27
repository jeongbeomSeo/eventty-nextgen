package com.eventty.eventtynextgen.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UserChangePasswordRequestCommand(

    @NotNull(message = "id값은 필수값입니다.")
    Long userId,
    @Length(min = 8, max = 16, message = "패스워드는 8자 이상 16자 이하여야 합니다.")
    String currentPassword,
    @Length(min = 8, max = 16, message = "패스워드는 8자 이상 16자 이하여야 합니다.")
    String updatedPassword,
    // TODO: PasswordValidator 적용
    String updatedPasswordConfirm
){
}
