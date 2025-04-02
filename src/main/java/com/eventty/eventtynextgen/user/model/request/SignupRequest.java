package com.eventty.eventtynextgen.user.model.request;

import com.eventty.eventtynextgen.user.model.UserRole;
import com.eventty.eventtynextgen.user.shared.annotation.EmailRegexp;
import com.eventty.eventtynextgen.user.shared.annotation.ValidBirthDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SignupRequest {

    @EmailRegexp
    private String email;
    @Length(min = 8, max = 16, message = "패스워드는 8자 이상 16자 이하여야 합니다.")
    private String password;
    @NotNull(message = "사용자 역할을 USER 혹은 HOST이어야 합니다.")
    private UserRole userRole;
    @NotBlank(message = "이름은 null이거나 빈 문자열일 수 없습니다.")
    private String name;
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "핸드폰 번호는 000-0000-0000의 형식이어야 합니다.")
    private String phone;
    @ValidBirthDate(message = "생년월일은 YYYY.MM.DD 혹은 YYYY-MM-DD 형식이어야 합니다.")
    private String birth;
}
