package com.eventty.eventtynextgen.base.filter;

import static com.eventty.eventtynextgen.base.constant.BaseConst.AUTHORIZATION_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.base.fixture.SessionTokenFixture;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.SessionTokenInfo;
import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.context.SessionContext;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.VerifyTokenResult;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Seesion Check Filter 단위 테스트")
class SessionCheckFilterTest {

    @BeforeEach
    void setUp() {
        SessionContextHolder.clearContext();
    }

    @Test
    @DisplayName("Session Token이 존재하지 않는 경우 요청은 넘어간다.")
    void Session_Token이_존재하지_않는_경우_요청은_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter();

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Session Token의 유효성 검증에 성공할 경우 Context에 SessionToken과 userId 그리고 검증결과가 업데이트되고 요청이 넘어간다")
    void Session_Token의_유효성_검증에_성공할_경우_Context에_SessionToken과_userId와_검증결과가_업데이트되고_요청이_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Long userId = 1L;
        SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, 60 * 1000L, 60 * 1000L);
        String accessTokenHeaderValue = sessionToken.getTokenType() + " " + sessionToken.getAccessToken();

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);

        doAnswer(invocation -> {
            // doFilter() 직전에 Context 상태 확인
            SessionContext sessionContext = SessionContextHolder.getContext();
            assertThat(sessionContext.getUserId()).isEqualTo(userId);
            assertThat(sessionContext.getVerifyTokenResult()).isEqualTo(VerifyTokenResult.VERIFIED_TOKEN);
            assertThat(sessionContext.getSessionToken()).isEqualTo(sessionToken.getAccessToken());

            return null;
        }).when(filterChain).doFilter(request, response);

        // when
        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter();
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Session Token의 만료 시간이 지난 경우 Context에 SessionToken과 검증결과가 업데이트되고 요청이 넘어간다")
    void Session_Token의_만료_시간이_지난_경우_Context에_SessionToken과_검증결과가_업데이트되고_요청이_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Long userId = 1L;
        SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60 * 1000L, -60 * 1000L);
        String accessTokenHeaderValue = sessionToken.getTokenType() + " " + sessionToken.getAccessToken();

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);

        doAnswer(invocation -> {
            // doFilter() 직전에 Context 상태 확인
            SessionContext sessionContext = SessionContextHolder.getContext();
            assertThat(sessionContext.getUserId()).isNull();
            assertThat(sessionContext.getVerifyTokenResult()).isEqualTo(VerifyTokenResult.EXPIRED_TOKEN);
            assertThat(sessionContext.getSessionToken()).isEqualTo(sessionToken.getAccessToken());

            return null;
        }).when(filterChain).doFilter(request, response);


        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter();

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Session Token이 이상한 문자열로 구성되어 있을 경우 token parsing을 실패하고 요청이 넘어간다")
    void Session_Token이_이상한_문자열로_구성되어_있을_경우_token_parsing을_실패하고_요청이_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        String accessTokenHeaderValue = "NOT_SUPPORTED_ACCESS_TOKEN_VALUE";

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);

        doAnswer(invocation -> {
            // doFilter() 직전에 Context 상태 확인
            SessionContext sessionContext = SessionContextHolder.getContext();
            assertThat(sessionContext.getUserId()).isNull();
            assertThat(sessionContext.getVerifyTokenResult()).isEqualTo(null);
            assertThat(sessionContext.getSessionToken()).isEqualTo(null);

            return null;
        }).when(filterChain).doFilter(request, response);

        // when
        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter();
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Session Token의 서명이 잘못되어 있는 경우 Context에 SessionToken과 검증결과가 업데이트되고 요청이 넘어간다")
    void Session_Token의_서명이_잘못되어_있는_경우_Context에_SessionToken과_검증결과가_업데이트되고_요청이_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(1L, 60 * 1000L, 60 * 1000L);
        String invalidToken = sessionToken.getAccessToken().substring(0, sessionToken.getAccessToken().lastIndexOf('.') + 1) + "invalidSignature";
        String accessTokenHeaderValue = sessionToken.getTokenType() + " " + invalidToken;

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);

        doAnswer(invovcation -> {
            // doFilter() 직전에 Context 상태 확인
            SessionContext sessionContext = SessionContextHolder.getContext();
            assertThat(sessionContext.getUserId()).isNull();
            assertThat(sessionContext.getVerifyTokenResult()).isEqualTo(VerifyTokenResult.INVALID_SIGNATURE_TOKEN);
            assertThat(sessionContext.getSessionToken()).isEqualTo(invalidToken);

            return null;
        }).when(filterChain).doFilter(request, response);

        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter();

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }
}