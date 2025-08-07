package com.eventty.eventtynextgen.shared.utils;

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

    public static void addLaxCookie(String name, String value, long maxAgeSeconds, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .path("/")
            .sameSite("Lax")
            .domain("localhost")
            .maxAge(maxAgeSeconds)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void removeLaxCookie(String name, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
            .path("/")
            .sameSite("Lax")
            .domain("localhost")
            .maxAge(0)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
