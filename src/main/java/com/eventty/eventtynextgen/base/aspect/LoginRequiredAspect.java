package com.eventty.eventtynextgen.base.aspect;

import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import com.eventty.eventtynextgen.base.exception.enums.ErrorType;
import com.eventty.eventtynextgen.base.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.context.SessionContext;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.VerifyTokenResult;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.FAILED_TOKEN_VERIFIED;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.ILLEGAL_STATE_JWT_TOKEN;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.INVALID_SIGNATURE_JWT_TOKEN;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.JWT_TOKEN_EXPIRED;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.LOGIN_REQUIRED_API;
import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.UNSUPPORTED_JWT_TOKEN;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoginRequiredAspect {

    private final UserComponent userComponent;

    @Before("@annotation(loginRequired)")
    public void checkAuthority(JoinPoint joinPoint, LoginRequired loginRequired) {
        if (loginRequired.requireLogin()) {
            // 1. Session Token 파싱에 성공했는지 확인
            SessionContext context = SessionContextHolder.getContext();
            if (!context.isSuccessParsingToken()) {
                VerifyTokenResult verifyTokenResult = context.getVerifyTokenResult();
                ErrorType errorType = getErrorTypeFromVerifyTokenResult(verifyTokenResult);
                log.debug("토큰의 검증에 실패했습니다. token value: {}, errorType Code: {}, errorType Msg: {}", context.getSessionToken(), errorType.getCode(), errorType.getMsg());
                if (errorType == AuthErrorType.UNKNOWN_EXCEPTION) {
                    throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, AuthErrorType.UNKNOWN_EXCEPTION);
                } else {
                    throw CustomException.of(HttpStatus.UNAUTHORIZED, errorType);
                }
            }

            // 2. 사용자 조회
            User user = userComponent.getUserByUserId(context.getUserId()).orElseThrow(
                () -> {
                    log.debug("사용자 조회에 실패했습니다. userId: {}", context.getUserId());
                    return CustomException.of(HttpStatus.UNAUTHORIZED, UserErrorType.NOT_FOUND_USER);
                }
            );

            // 3. 사용자 검증
            if (user.isDeleted()) {
                log.debug("삭제된 사용자이므로 검증에 실패했습니다. userId: {}", context.getUserId());
                throw CustomException.of(HttpStatus.UNAUTHORIZED, UserErrorType.USER_ALREADY_DELETED);
            }

            // 4. 역할 기반 API 호출 권한 검증
            String userRole = user.getUserRole().name();
            if (requiredRole(loginRequired) && !checkAuthority(loginRequired, userRole)) {
                // 1개라도 일치하는 권한이 없을 경우
                log.debug("사용자에게 API 호출 권한이 없습니다. requiredRole Admin: {}, Host: {}, User: {}, userRole: {}", loginRequired.requireAdmin(), loginRequired.requireHost(), loginRequired.requireUser(), userRole);
                throw CustomException.of(HttpStatus.FORBIDDEN, AuthErrorType.AUTH_USER_NOT_AUTHORIZED);
            }

            // 5. 사용자 역할 정보 업데이트
            context.updateRole(userRole);
        }
    }

    private ErrorType getErrorTypeFromVerifyTokenResult(VerifyTokenResult verifyTokenResult) {
        if (verifyTokenResult == null) {
            return LOGIN_REQUIRED_API;
        }

        switch (verifyTokenResult) {
            case EXPIRED_TOKEN -> {
                return JWT_TOKEN_EXPIRED;
            }
            case UNSUPPORTED_TOKEN -> {
                return UNSUPPORTED_JWT_TOKEN;
            }
            case ILLEGAL_STATE_TOKEN -> {
                return ILLEGAL_STATE_JWT_TOKEN;
            }
            case INVALID_SIGNATURE_TOKEN -> {
                return INVALID_SIGNATURE_JWT_TOKEN;
            }
            case UNKNOWN_ERROR -> {
                return FAILED_TOKEN_VERIFIED;
            }
            default -> {
                return AuthErrorType.UNKNOWN_EXCEPTION;
            }
        }
    }

    private boolean checkAuthority(LoginRequired loginRequired, String userRole) {
        // ADMIN 권한이 필요한 경우
        UserRoleType userRoleType = UserRoleType.valueOf(userRole);

        if (loginRequired.requireAdmin() && UserRoleType.ADMIN == userRoleType) {
            return true;
        }

        // HOST 권한이 필요한 경우
        if (loginRequired.requireHost() && UserRoleType.HOST == userRoleType) {
            return true;
        }

        // USER 권한이 필요한 경우
        if (loginRequired.requireUser() && UserRoleType.USER == userRoleType) {
            return true;
        }

        return false;
    }

    private boolean requiredRole(LoginRequired loginRequired) {
        return loginRequired.requireAdmin() || loginRequired.requireHost() || loginRequired.requireUser();
    }
}
