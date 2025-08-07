package com.eventty.eventtynextgen.shared.utils;


import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

@DisplayName("Cookie Utility Class 단위 테스트")
class CookieUtilsTest {

    @Nested
    @DisplayName("Lax Cookie 생성 테스트")
    class CreateLaxCookie {
        @Test
        @DisplayName("Response 인자에 Lax Cookie를 성공적으로 추가한다.")
        void Response_인자에_Lax_Cookie를_성공적으로_추가한다() {
            // given
            String name = "cookieName";
            String value = "cookieValue";
            long maxAgeSeconds = 1000L;
            MockHttpServletResponse response = new MockHttpServletResponse();

            // when
            CookieUtils.addLaxCookie(name, value, maxAgeSeconds, response);

            // then
            assertThat(response.getCookies().length).isEqualTo(1);
            assertThat(response.getCookie(name).getName()).isEqualTo(name);
            assertThat(response.getCookie(name).getValue()).isEqualTo(value);
            assertThat(response.getCookie(name).getMaxAge()).isEqualTo(maxAgeSeconds);
        }
    }

    @Nested
    @DisplayName("Lax Cookie 삭제 테스트")
    class RemoveLaxCookie {
        @Test
        @DisplayName("Response 인자의 쿠키를_name을_통해 성공적으로 제거한다.")
        void Response_인자의_쿠키를_name을_통해_성공적으로_제거한다() {
            // given
            String name = "cookieName";
            MockHttpServletResponse response = new MockHttpServletResponse();

            // when
            CookieUtils.removeLaxCookie(name, response);

            // then
            assertThat(response.getCookie(name).getMaxAge()).isEqualTo(0);
            assertThat(response.getCookie(name).getValue()).isEqualTo("");
        }
    }
}