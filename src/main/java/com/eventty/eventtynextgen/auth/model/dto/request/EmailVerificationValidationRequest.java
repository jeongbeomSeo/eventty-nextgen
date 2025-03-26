package com.eventty.eventtynextgen.auth.model.dto.request;

import com.eventty.eventtynextgen.auth.shared.annotation.EmailRegexp;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailVerificationValidationRequest {

    @EmailRegexp
    private String email;
    @NotBlank(message = "인증 코드는 필수입니다.")
    private String code;
}
