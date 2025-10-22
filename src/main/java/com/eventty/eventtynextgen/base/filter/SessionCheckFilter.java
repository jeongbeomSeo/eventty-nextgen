package com.eventty.eventtynextgen.base.filter;

import static com.eventty.eventtynextgen.base.constant.BaseConst.*;
import static com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.*;
import static com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.VerifyTokenResult.VERIFIED_TOKEN;

import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.VerifyTokenResult;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Order(-1)
@RequiredArgsConstructor
@Component
public class SessionCheckFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청 헤더로부터 Session Token 가져오기
        String sessionToken = parseBearerToken(request);

        if (sessionToken == null) {
            log.debug("헤더로부터 SessionToken을 추출하는데 실패했습니다. URI: {}, Header value: {}", request.getRequestURI(), request.getHeader(AUTHORIZATION_HEADER));
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Session Token 파싱
            VerifyTokenResult verifyTokenResult = verifyToken(sessionToken);
            if (verifyTokenResult != VERIFIED_TOKEN) {
                log.debug("SessionToken 검증에 실패했습니다. URI: {}, VerifyTokenResult: {}", request.getRequestURI(), verifyTokenResult);
                SessionContextHolder.getContext().updateSessionInfo(sessionToken, verifyTokenResult);
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Session Token으로부터 사용자 ID 추출 및 업데이트
            Long userId = extractAccessTokenPayloadIgnoringExpiredExpiration(sessionToken).getUserId();
            SessionContextHolder.getContext().updateSessionInfo(userId, sessionToken, verifyTokenResult);
            filterChain.doFilter(request, response);
        } finally {
            SessionContextHolder.clearContext();
        }
    }

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        int beginIndex = JWT_TOKEN_TYPE.length() + 1;

        if (StringUtils.hasText(bearerToken) && bearerToken.length() > beginIndex && bearerToken.startsWith(JWT_TOKEN_TYPE)) {
            return bearerToken.substring(beginIndex);
        }

        return null;
    }
}
