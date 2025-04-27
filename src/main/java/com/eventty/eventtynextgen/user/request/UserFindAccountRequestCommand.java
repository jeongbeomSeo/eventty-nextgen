package com.eventty.eventtynextgen.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserFindAccountRequestCommand(
    @NotBlank(message = "이름은 null이거나 빈 문자열일 수 없습니다.")
    String name,
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "핸드폰 번호는 000-0000-0000의 형식이어야 합니다.")
    String phone
) {

}
