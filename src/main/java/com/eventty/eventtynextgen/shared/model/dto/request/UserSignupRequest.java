package com.eventty.eventtynextgen.shared.model.dto.request;

import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.shared.validator.BirthDateValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserSignupRequest {

    private Long authUserId;
    private String name;
    private String phone;
    private String birth;

    public UserSignupRequest(Long authUserId, SignupRequest signupRequest) {
        this(authUserId, signupRequest.getName(), signupRequest.getPhone(),
            signupRequest.getBirth());
    }

    /**
     * 회원가입 요청 시 필요한 필드에 대해 null/빈 값 및 형식 검증을 수행한다.
     * <p>
     * <ul>
     *     <li>auth user id는 필수값이다.</li>
     *     <li>name은 필수값이다.</li>
     *     <li>phone은 필수값이며, `000-0000-0000` 형식을 만족해야 한다.</li>
     *     <li>birth는 필수값이며, `YYYY.MM.DD` 또는 `YYYY-MM-DD` 형식을 만족해야 한다.</li>
     * </ul>
     * </p>
     */
    public UserSignupRequest(Long authUserId, String name, String phone, String birth) {
        // 필수값 검증
        Assert.notNull(authUserId, "authUserID 값은 필수값 입니다.");
        Assert.hasText(name, "이름은 필수값 입니다.");
        Assert.hasText(phone, "핸드폰 번호는 필수값 입니다.");
        Assert.hasText(birth, "생년월일은 필수값 입니다.");

        validatePhoneFormat(phone);
        validateBirthDate(birth);

        this.authUserId = authUserId;
        this.name = name;
        this.phone = phone;
        this.birth = birth;
    }

    /**
     * 핸드폰 번호 형식을 검증한다. 형식: `000-0000-0000`
     * @param phone 핸드폰 번호 문자열
     * @throws IllegalArgumentException 핸드폰 번호 형식이 올바르지 않을 경우 예외 발생
     */
    private void validatePhoneFormat(String phone) {
        if (!phone.matches("^\\d{3}-\\d{4}-\\d{4}$")) {
            throw new IllegalArgumentException("핸드폰 번호 형식이 올바르지 않습니다. 형식: 000-0000-0000");
        }
    }

    /**
     * 생년월일 형식을 검증한다. 형식: `YYYY.MM.DD` 또는 `YYYY-MM-DD`
     * @param birth 생년월일 문자열
     * @throws IllegalArgumentException 생년월일 형식이 올바르지 않을 경우 예외 발생
     */
    private void validateBirthDate(String birth) {
        BirthDateValidator birthDateValidator = new BirthDateValidator();

        if (!birthDateValidator.isValid(birth, null)) {
            throw new IllegalArgumentException("생년 월일 형식이 올바르지 않습니다. 형식: `YYYY.MM.DD` 또는 `YYYY-MM-DD`");
        }
    }
}

