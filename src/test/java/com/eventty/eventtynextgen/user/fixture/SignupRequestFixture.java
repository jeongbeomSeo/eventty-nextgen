package com.eventty.eventtynextgen.user.fixture;

import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import com.eventty.eventtynextgen.user.request.UserRequestCommand;

public class SignupRequestFixture {

    /**
     * USER 역할을 가진 성공적인 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserRequestCommand successUserRoleRequest() {
        return new UserRequestCommand("test@google.com","12345678", UserRoleType.USER, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * HOST 역할을 가진 성공적인 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserRequestCommand successHostRoleRequest() {
        return new UserRequestCommand("test@google.com", "12345678", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990.12.31");
    }

    /**
     * 이메일에 @가 빠져있는 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserRequestCommand missingAtSymbolInEmailRequest() {
        return new UserRequestCommand("testgoogle.com", "12345678", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 이메일에 .가 빠져있는 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserRequestCommand missingDotInEmailRequest() {
        return new UserRequestCommand("test@googlecom", "12345678", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 패스워드가 8자 미만인 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserRequestCommand shortPasswordRequest() {
        return new UserRequestCommand("test@google.com", "12345", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 패스워드가 16자 초과인 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserRequestCommand longPasswordRequest() {
        return new UserRequestCommand("test@google.com", "12345678901234567", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "1990-01-01");
    }

    public static UserRequestCommand nameIsNullRequest() {
        return new UserRequestCommand("test@google.com","12345678", UserRoleType.HOST, null, "000-0000-0000",
            "1990-01-01");
    }

    public static UserRequestCommand nameIsEmptyRequest() {
        return new UserRequestCommand("test@google.com","12345678", UserRoleType.HOST, "", "000-0000-0000",
            "1990-01-01");
    }

    /**
     * 핸드폰 형식이 000-0000-0000가 아닌 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserRequestCommand invalidPhoneNumberRequest() {
        return new UserRequestCommand("test@google.com", "12345678", UserRoleType.HOST, "홍길동", "0000000000",
            "1990-01-01");
    }

    /**
     * 생년 월일이 yyyy.mm.dd 혹은 yyyy-mm-dd 형식이 아닌 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserRequestCommand invalidBirthdateFormatRequest() {
        return new UserRequestCommand("test@google.com", "12345678", UserRoleType.HOST, "홍길동", "000-0000-0000",
            "19900101");
    }

    /**
     * 사용자 역할이 올바르지 않은 Request를 생성한다.
     * @return SignupRequest
     */
    public static UserRequestCommand invalidUserRoleRequest() {
        return new UserRequestCommand("test@google.com", "12345678", null, "홍길동", "000-0000-0000",
            "1990-01-01");
    }
}
