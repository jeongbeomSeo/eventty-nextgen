package com.eventty.eventtynextgen.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UserChangePasswordRequestCommand(

    @Schema(description = "사용자 PK")
    @NotNull(message = "id값은 필수값입니다.")
    Long userId,
    @Schema(description = "현재 사용자 비밀번호", minLength = 8, maxLength = 16)
    @Length(min = 8, max = 16, message = "패스워드는 8자 이상 16자 이하여야 합니다.")
    String currentPassword,
    @Schema(description = "변경할 비밀번호", minLength = 8, maxLength = 16)
    @Length(min = 8, max = 16, message = "패스워드는 8자 이상 16자 이하여야 합니다.")
    String updatedPassword
){
}
