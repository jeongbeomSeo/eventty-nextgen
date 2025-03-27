package com.eventty.eventtynextgen.auth.model.dto.request;

import com.eventty.eventtynextgen.auth.shared.annotation.EmailRegexp;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailVerificationRequest {

    @EmailRegexp
    private String email;
}
