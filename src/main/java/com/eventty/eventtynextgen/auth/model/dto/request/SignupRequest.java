package com.eventty.eventtynextgen.auth.model.dto.request;

import com.eventty.eventtynextgen.auth.model.UserRole;
import com.eventty.eventtynextgen.auth.shared.annotation.ValidBirthDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
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

    @Email(message = "이메일 형식이 올바르지 않습니다.", regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    private String email;
    @Length(min = 8, max = 16, message = "패스워드는 최소 8자 이상, 16자 이하 이여야만 합니다.")
    private String password;
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "핸드폰 번호는 000-0000-0000 형식이어야 합니다.")
    private String phone;
    @ValidBirthDate(message = "생년월일은 yyyy.mm.dd 혹은 yyyy-mm-dd 형식이어야 합니다.")
    private String birth;
    @NotNull(message = "사용자의 역할을 지정해주세요.")
    private UserRole userRole;
}
