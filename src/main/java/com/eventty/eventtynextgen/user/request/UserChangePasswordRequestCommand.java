package com.eventty.eventtynextgen.user.request;

import com.eventty.eventtynextgen.user.shared.annotation.PasswordMatch;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@PasswordMatch
public record UserChangePasswordRequestCommand(

    @NotNull(message = "id값은 필수값입니다.")
    Long userId,
    @Length(min = 8, max = 16, message = "패스워드는 8자 이상 16자 이하여야 합니다.")
    String currentPassword,
    @Length(min = 8, max = 16, message = "패스워드는 8자 이상 16자 이하여야 합니다.")
    String updatedPassword,
    @Length(min = 8, max = 16, message = "패스워드는 8자 이상 16자 이하여야 합니다.")
    String updatedPasswordConfirm
){
}
