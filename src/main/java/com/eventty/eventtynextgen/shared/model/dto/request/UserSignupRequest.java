package com.eventty.eventtynextgen.shared.model.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserSignupRequest {

    private Long authUserId;

    private String name;

    private String phone;

    private String birth;
}
