package com.eventty.eventtynextgen.user.model.request;

import com.eventty.eventtynextgen.user.shared.annotation.EmailRegexp;
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
