package com.eventty.eventtynextgen.user.request;

import com.eventty.eventtynextgen.user.annotation.ValidBirthDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequestCommand(
    @Schema(description = "사용자 PK")
    @NotNull(message = "id값은 필수값입니다.")
    Long id,
    @Schema(description = "변경할 이름")
    @NotBlank(message = "이름은 null이거나 빈 문자열일 수 없습니다.")
    String name,
    @Schema(description = "변경할 전화번호")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "핸드폰 번호는 000-0000-0000의 형식이어야 합니다.")
    String phone,
    @Schema(description = "변경할 생년월일")
    @ValidBirthDate(message = "생년월일은 YYYY.MM.DD 혹은 YYYY-MM-DD 형식이어야 합니다.")
    String birth
){
}
