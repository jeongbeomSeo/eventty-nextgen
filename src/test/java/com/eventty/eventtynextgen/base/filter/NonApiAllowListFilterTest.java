package com.eventty.eventtynextgen.base.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("제어권이 없는 API 요청 필터 단위 테스트")
class NonApiAllowListFilterTest {

    @Mock
    private ResponseUtils responseUtils;

    @ParameterizedTest(name = "[{index}] URI: {0}")
    @MethodSource("skipPatternArguments")
    @DisplayName("예외 URL 패턴이 올바르게 적용되는지 확인 테스트")
    void 예외_URL_패턴이_올바르게_적용되는지_확인_테스트(String fixtureName, String requestURI) throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn(requestURI);

        NonApiAllowListFilter nonApiAllowListFilter = new NonApiAllowListFilter(responseUtils);

        // when
        nonApiAllowListFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(responseUtils, times(0)).writeErrorResponseToResponse(any(HttpServletResponse.class), any(CustomException.class));
    }

    @Test
    @DisplayName("예외 URL 패턴이 아닌 경우 요청은 필터링되어 예외 데이터와 같이 응답된다")
    void 예외_URL_패턴이_아닌_경우_요청은_필터링되어_예외_데이터와_같이_응답된다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        String requestURI = "/attack";

        when(request.getRequestURI()).thenReturn(requestURI);
        doNothing().when(responseUtils).writeErrorResponseToResponse(any(HttpServletResponse.class), any(CustomException.class));

        NonApiAllowListFilter nonApiAllowListFilter = new NonApiAllowListFilter(responseUtils);

        // when
        nonApiAllowListFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
        verify(responseUtils, times(1)).writeErrorResponseToResponse(any(HttpServletResponse.class), any(CustomException.class));
    }


    private static Stream<Arguments> skipPatternArguments() {
        return Stream.of(
            Arguments.of("/api/**",
                "/api/v1/users"),
            Arguments.of("/swagger-ui/**",
                "/swagger-ui/index.html"),
            Arguments.of("/v3/api-docs/**",
                "/v3/api-docs/swagger-config"),
            Arguments.of("/health/**",
                "/health/check"),
            Arguments.of("/health",
                "/health"),
            Arguments.of("/actuator/**",
                "/actuator/health"),
            Arguments.of("/css/**",
                "/css/main.css"),
            Arguments.of("/js/**",
                "/js/app.js"),
            Arguments.of("/images/**",
                "/images/logo.png"),
            Arguments.of("/webjars/**",
                "/webjars/bootstrap/5.1.3/css/bootstrap.min.css"),
            Arguments.of("/fonts/**",
                "/fonts/roboto.woff2"),
            Arguments.of("/resources/**",
                "/resources/application.properties"),
            Arguments.of("/static/**",
                "/static/index.html"),
            Arguments.of("/error/**",
                "/error/404"),
            Arguments.of("/.well-known/**",
                "/.well-known/security.txt"),
            Arguments.of("/favicon.ico",
                "/favicon.ico"),
            Arguments.of("/robots.txt",
                "/robots.txt")
        );
    }
}