package com.eventty.eventtynextgen.certification.shared.utils;

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
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .domain("localhost")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void removeRefreshToken(HttpServletResponse response) {
        ResponseCookie removedRefreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_HEADER_NAME, "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .domain("localhost")
            .maxAge(0)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, removedRefreshTokenCookie.toString());
    }
}
