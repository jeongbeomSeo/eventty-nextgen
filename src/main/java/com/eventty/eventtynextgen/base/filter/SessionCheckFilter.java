package com.eventty.eventtynextgen.base.filter;

import static com.eventty.eventtynextgen.base.constant.BaseConst.*;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.FAILED_TOKEN_VERIFIED;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.ILLEGAL_STATE_JWT_TOKEN;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.INVALID_SIGNATURE_JWT_TOKEN;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.JWT_TOKEN_EXPIRED;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.UNSUPPORTED_JWT_TOKEN;
import static com.eventty.eventtynextgen.base.provider.JwtTokenProvider.VerifyTokenResult.VERIFIED_TOKEN;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.VerifyTokenResult;
import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.context.SessionContext;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.base.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Order(-1)
@RequiredArgsConstructor
@Component
public class SessionCheckFilter extends OncePerRequestFilter {

    private final UserComponent userComponent;
    private final ResponseUtils responseUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청 헤더로부터 Session Token 가져오기
        String sessionToken = parseBearerToken(request);

        if (sessionToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Session Token 파싱
        if (!verifySessionToken(sessionToken, response)) {
            return;
        }

        // 3. userId를 통해 사용자 조회
        User user = getUserBySessionToken(sessionToken, response);
        if (user == null) {
            return;
        }

        // 4. 사용자 검증
        if (!validateUser(user, response)) {
            return;
        }

        try {
            // 5. Context 업데이트
            SessionContext sessionContext = SessionContextHolder.getContext();
            sessionContext.updateSessionInfo(user.getId(), user.getUserRole().name());

            filterChain.doFilter(request, response);
        } finally {
            SessionContextHolder.clearContext();
        }
    }

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JWT_TOKEN_TYPE)) {
            return bearerToken.substring(JWT_TOKEN_TYPE.length() + 1);
        }

        return null;
    }

    private boolean verifySessionToken(String sessionToken, HttpServletResponse response) {
        VerifyTokenResult verifyTokenResult = JwtTokenProvider.verifyToken(sessionToken);
        if (verifyTokenResult != VERIFIED_TOKEN) {
            CustomException customException;
            switch (verifyTokenResult) {
                case EXPIRED_TOKEN -> {
                    customException = CustomException.badRequest(JWT_TOKEN_EXPIRED);
                }
                case UNSUPPORTED_TOKEN -> {
                    customException = CustomException.badRequest(UNSUPPORTED_JWT_TOKEN);
                }
                case ILLEGAL_STATE_TOKEN -> {
                    customException = CustomException.badRequest(ILLEGAL_STATE_JWT_TOKEN);
                }
                case INVALID_SIGNATURE_TOKEN -> {
                    customException = CustomException.badRequest(INVALID_SIGNATURE_JWT_TOKEN);
                }
                case UNKNOWN_ERROR -> {
                    customException = CustomException.badRequest(FAILED_TOKEN_VERIFIED);
                }
                default -> {
                    customException = CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, AuthErrorType.UNKNOWN_EXCEPTION);
                }
            }
            this.responseUtils.writeErrorResponseToResponse(response, customException);
            return false;
        }

        return true;
    }
    private User getUserBySessionToken(String sessionToken, HttpServletResponse response) {
        Long userId = JwtTokenProvider.retrieveSessionTokenPayload(sessionToken).getUserId();
        Optional<User> userOpt = userComponent.getUserByUserId(userId);

        if (userOpt.isEmpty()) {
            CustomException customException = CustomException.badRequest(UserErrorType.NOT_FOUND_USER);
            this.responseUtils.writeErrorResponseToResponse(response, customException);
            return null;
        }

        return userOpt.get();
    }
    private boolean validateUser(User user, HttpServletResponse response) {
        if (user.isDeleted()) {
            CustomException customException = CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED);
            this.responseUtils.writeErrorResponseToResponse(response, customException);
            return false;
        }

        return true;
    }
}
