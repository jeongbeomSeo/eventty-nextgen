package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import com.eventty.eventtynextgen.user.request.UserSignUpRequestCommand;

public class SignupRequestFixture {

    /**
     * USER 역할을 가진 성공적인 Request를 생성한다.
     * @param email
     * @return SignupRequest
     */
    public static UserSignUpRequestCommand successUserSignUpRequest(String email) {
        return new UserSignUpRequestCommand(email,"12345678", UserRoleType.USER, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * HOST 역할을 가진 성공적인 Request를 생성한다.
     * @param email
     * @return SignupRequest
     */
    public static UserSignUpRequestCommand successHostRoleRequest(String email) {
        return new UserSignUpRequestCommand(email, "12345678", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990.12.31");
    }

    /**
     * 이메일에 @가 빠져있는 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserSignUpRequestCommand missingAtSymbolInEmailRequest() {
        return new UserSignUpRequestCommand("testnaver.com", "12345678", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 이메일에 .가 빠져있는 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserSignUpRequestCommand missingDotInEmailRequest() {
        return new UserSignUpRequestCommand("test@navercom", "12345678", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 패스워드가 8자 미만인 Request를 생성한다.
     * @return SignupRequest
     * @param email
     */
    public static UserSignUpRequestCommand shortPasswordRequest(String email) {
        return new UserSignUpRequestCommand(email, "12345", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 패스워드가 16자 초과인 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserSignUpRequestCommand longPasswordRequest(String email) {
        return new UserSignUpRequestCommand(email, "12345678901234567", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    public static UserSignUpRequestCommand nameIsNullRequest(String email) {
        return new UserSignUpRequestCommand(email,"12345678", UserRoleType.HOST, null, "000-0000-0000",
            "1990-01-01");
    }

    public static UserSignUpRequestCommand nameIsEmptyRequest(String email) {
        return new UserSignUpRequestCommand(email,"12345678", UserRoleType.HOST, "", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 핸드폰 형식이 000-0000-0000가 아닌 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserSignUpRequestCommand invalidPhoneNumberRequest(String email) {
        return new UserSignUpRequestCommand(email, "12345678", UserRoleType.HOST, "홍길동", "0000000000",
            "1990-01-01");
    }

    /**
     * 생년 월일이 yyyy.mm.dd 혹은 yyyy-mm-dd 형식이 아닌 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserSignUpRequestCommand invalidBirthdateFormatRequest(String email) {
        return new UserSignUpRequestCommand(email, "12345678", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "19900101");
    }

    /**
     * 사용자 역할이 올바르지 않은 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserSignUpRequestCommand invalidUserRoleRequest(String email) {
        return new UserSignUpRequestCommand(email, "12345678", null, "홍길동", "000-0000-0000",
            "1990-01-01");
    }
}
