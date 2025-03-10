package com.eventty.eventtynextgen.auth.model.dto.request;

import com.eventty.eventtynextgen.auth.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupRequest {
    @Email(message = "이메일 형식이 올바르지 않습니다.", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private String email;
    @Min(value = 8, message = "패스워드는 최소 8자 이상이여야만 합니다.")
    private String password;
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "핸드폰 번호는 000-0000-0000 형식이어야 합니다.")
    private String phone;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일은 yyyy-MM-dd 형식이어야 합니다.")
    private String birth;
    @NotNull(message = "사용자의 역할을 지정해주세요.")
    private UserRole userRole;
}
