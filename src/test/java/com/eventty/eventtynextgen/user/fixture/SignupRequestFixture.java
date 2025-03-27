package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.model.UserRole;
import com.eventty.eventtynextgen.user.model.request.SignupRequest;

public class SignupRequestFixture {

    /**
     * USER 역할을 가진 성공적인 Request를 생성한다.
     * @return SignupRequest
     */
    public static SignupRequest successUserRoleRequest() {
        return new SignupRequest("test@google.com","12345678", UserRole.USER, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * HOST 역할을 가진 성공적인 Request를 생성한다.
     * @return SignupRequest
     */
    public static SignupRequest successHostRoleRequest() {
        return new SignupRequest("test@google.com", "12345678", UserRole.HOST, "홍길동", "000-0000-0000",
            "1990.12.31");
    }

    /**
     * 이메일에 @가 빠져있는 Request를 생성한다.
     * @return SignupRequest
     */
    public static SignupRequest missingAtSymbolInEmailRequest() {
        return new SignupRequest("testgoogle.com", "12345678", UserRole.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 이메일에 .가 빠져있는 Request를 생성한다.
     * @return SignupRequest
     */
    public static SignupRequest missingDotInEmailRequest() {
        return new SignupRequest("test@googlecom", "12345678", UserRole.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 패스워드가 8자 미만인 Request를 생성한다.
     * @return SignupRequest
     */
    public static SignupRequest shortPasswordRequest() {
        return new SignupRequest("test@google.com", "12345", UserRole.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 패스워드가 16자 초과인 Request를 생성한다.
     * @return SignupRequest
     */
    public static SignupRequest longPasswordRequest() {
        return new SignupRequest("test@google.com", "12345678901234567", UserRole.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    public static SignupRequest nameIsNullRequest() {
        return new SignupRequest("test@google.com","12345678", UserRole.HOST, null, "000-0000-0000",
            "1990-01-01");
    }

    public static SignupRequest nameIsEmptyRequest() {
        return new SignupRequest("test@google.com","12345678", UserRole.HOST, "", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 핸드폰 형식이 000-0000-0000가 아닌 Request를 생성한다.
     * @return SignupRequest
     */
    public static SignupRequest invalidPhoneNumberRequest() {
        return new SignupRequest("test@google.com", "12345678", UserRole.HOST, "홍길동", "0000000000",
            "1990-01-01");
    }

    /**
     * 생년 월일이 yyyy.mm.dd 혹은 yyyy-mm-dd 형식이 아닌 Request를 생성한다.
     * @return SignupRequest
     */
    public static SignupRequest invalidBirthdateFormatRequest() {
        return new SignupRequest("test@google.com", "12345678", UserRole.HOST, "홍길동", "000-0000-0000",
            "19900101");
    }

    /**
     * 사용자 역할이 올바르지 않은 Request를 생성한다.
     * @return SignupRequest
     */
    public static SignupRequest invalidUserRoleRequest() {
        return new SignupRequest("test@google.com", "12345678", null, "홍길동", "000-0000-0000",
            "1990-01-01");
    }
}
