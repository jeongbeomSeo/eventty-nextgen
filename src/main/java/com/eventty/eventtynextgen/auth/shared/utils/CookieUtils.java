package com.eventty.eventtynextgen.auth.shared.utils;

import com.eventty.eventtynextgen.auth.constant.AuthConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

@UtilityClass
public class CookieUtils {

    public static String REFRESH_TOKEN_HEADER_NAME = "refreshToken";

    public static String getCookie(String name, HttpServletRequest request) {
        return request.getHeader(name);
    }

    public static void addRefreshToken(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_HEADER_NAME, refreshToken)
            .path("/")
            .sameSite("Strict")
            .domain("localhost")
            .maxAge(10080L * 60)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void removeRefreshToken(HttpServletResponse response) {
        ResponseCookie removedRefreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_HEADER_NAME, "")
            .path("/")
            .sameSite("Strict")
            .domain("localhost")
            .maxAge(0)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, removedRefreshTokenCookie.toString());
    }
}
