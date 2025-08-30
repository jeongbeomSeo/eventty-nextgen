package com.eventty.eventtynextgen.base.filter;

import static com.eventty.eventtynextgen.base.constant.BaseConst.AUTHORIZATION_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.SessionTokenInfo;
import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.context.SessionContext;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Seesion Check Filter 단위 테스트")
class SessionCheckFilterTest {

    @Mock
    private UserComponent userComponent;

    @Mock
    private ResponseUtils responseUtils;

    @Test
    @DisplayName("Session Token이 존재하지 않는 경우 요청은 넘어간다.")
    void Session_Token이_존재하지_않는_경우_요청은_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter(userComponent, responseUtils);

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Session Token의 유효성 검증과 사용자 검증이 성공할 경우 Context는 업데이트되고 요청은 넘어간다")
    void Session_Token의_유효성_검증과_사용자_검증이_성공할_경우_Context는_업데이트되고_요청은_넘어간다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        User user = mock(User.class);

        Long userId = 1L;
        UserRoleType userRoleType = UserRoleType.USER;
        SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, 60 * 1000L, 60 * 1000L);
        String accessTokenHeaderValue = sessionToken.getTokenType() + "  " + sessionToken.getAccessToken();

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);
        when(userComponent.getUserByUserId(userId)).thenReturn(Optional.of(user));
        when(user.isDeleted()).thenReturn(false);
        when(user.getId()).thenReturn(userId);
        when(user.getUserRole()).thenReturn(userRoleType);

        doAnswer(invocation -> {
            // doFilter() 직전에 Context 상태 확인
            SessionContext sessionContext = SessionContextHolder.getContext();
            assertThat(sessionContext.getUserId()).isEqualTo(userId);
            assertThat(sessionContext.getRole()).isEqualTo(userRoleType.name());

            return null;
        }).when(filterChain).doFilter(request, response);

        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter(userComponent, responseUtils);

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Session Token의 만료 시간이 지났을 경우 요청은 필터링된다.")
    void Session_Token의_만료_시간이_지났을_경우_요청은_필터링된다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Long userId = 1L;
        SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60 * 1000L, -60 * 1000L);
        String accessTokenHeaderValue = sessionToken.getTokenType() + "  " + sessionToken.getAccessToken();

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);

        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter(userComponent, responseUtils);

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    @DisplayName("Session Token이 이상한 문자열로 되어 있을 경우 요청은 필터링된다.")
    void Session_Token이_이상한_문자열로_되어_있을_경우_요청은_필터링된다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Long userId = 1L;
        SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60 * 1000L, -60 * 1000L);
        String accessTokenHeaderValue = sessionToken.getTokenType() + "  " + "---" + sessionToken.getAccessToken().substring(3);

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);

        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter(userComponent, responseUtils);

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    @DisplayName("Session Token의 서명이 잘못되어 있는 경우 요청은 필터링된다.")
    void Session_Token의_서명이_잘못되어_있는_경우_요청은_필터링된다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Long userId = 1L;
        SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, -60 * 1000L, -60 * 1000L);
        String accessTokenHeaderValue = sessionToken.getTokenType() + "  " + sessionToken.getAccessToken().substring(3) + "---";

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);

        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter(userComponent, responseUtils);

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    @DisplayName("사용자가 삭제되어 있는 경우 요청은 필터링된다.")
    void 사용자가_삭제되어_있는_경우_요청은_필터링된다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        User user = mock(User.class);

        Long userId = 1L;
        SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, 60 * 1000L, 60 * 1000L);
        String accessTokenHeaderValue = sessionToken.getTokenType() + "  " + sessionToken.getAccessToken();

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);
        when(userComponent.getUserByUserId(userId)).thenReturn(Optional.of(user));
        when(user.isDeleted()).thenReturn(true);

        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter(userComponent, responseUtils);

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    @DisplayName("사용자를 찾을 수 없는 경우 요청은 필터링된다.")
    void 사용자를_찾을_수_없는_경우_요청은_필터링된다() throws ServletException, IOException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        Long userId = 1L;
        SessionTokenInfo sessionToken = JwtTokenProvider.createSessionToken(userId, 60 * 1000L, 60 * 1000L);
        String accessTokenHeaderValue = sessionToken.getTokenType() + "  " + sessionToken.getAccessToken();

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(accessTokenHeaderValue);
        when(userComponent.getUserByUserId(userId)).thenReturn(Optional.empty());

        SessionCheckFilter sessionCheckFilter = new SessionCheckFilter(userComponent, responseUtils);

        // when
        sessionCheckFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(0)).doFilter(request, response);
    }
}