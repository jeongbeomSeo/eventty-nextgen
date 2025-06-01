package com.eventty.eventtynextgen.user.request;

import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import com.eventty.eventtynextgen.user.annotation.EmailRegexp;
import com.eventty.eventtynextgen.user.annotation.ValidBirthDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record UserSignUpRequestCommand(
    @Schema(description = "사용자 이메일", pattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    @EmailRegexp
    String email,
    @Schema(description = "사용자 비밀번호", minLength = 8, maxLength = 16)
    @Length(min = 8, max = 16, message = "패스워드는 8자 이상 16자 이하여야 합니다.")
    String password,
    @Schema(description = "사용자 역할")
    @NotNull(message = "사용자 역할을 USER, HOST, ADMIN 중 하나여야 합니다.")
    UserRoleType userRole,
    @Schema(description = "사용자 이름")
    @NotBlank(message = "이름은 null이거나 빈 문자열일 수 없습니다.")
    String name,
    @Schema(description = "사용자 전화번호", pattern = "^\\d{3}-\\d{4}-\\d{4}$")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "핸드폰 번호는 000-0000-0000의 형식이어야 합니다.")
    String phone,
    @Schema(description = "사용자 생년월일", pattern = "^(\\d{4}[-.]\\d{2}[-.]\\d{2})$")
    @ValidBirthDate(message = "생년월일은 YYYY.MM.DD 혹은 YYYY-MM-DD 형식이어야 합니다.")
    String birth
){
}
