package com.eventty.eventtynextgen.base.aspect;

import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import com.eventty.eventtynextgen.auth.authorization.enums.AuthorizationType;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.AuthErrorType;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginRequiredAspect {

    @Before("@annotation(loginRequired)")
    public void checkAuthority(JoinPoint joinPoint, LoginRequired loginRequired) {
        if (loginRequired.loginRequired()) {
            if (!AuthorizationContextHolder.getContext().validate()) {
                throw CustomException.of(HttpStatus.FORBIDDEN, AuthErrorType.AUTH_USER_NOT_AUTHORIZED);
            }

            // API 호출 권한이 필요한 경우
            if (requiredRole(loginRequired)) {
                Set<String> userRoles = Arrays.stream(AuthorizationContextHolder.getContext().getRole().split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());

                if (!checkAuthority(loginRequired, userRoles)) {
                    // 1개라도 일치하는 권한이 없을 경우
                    throw CustomException.of(HttpStatus.FORBIDDEN, AuthErrorType.AUTH_USER_NOT_AUTHORIZED);
                }
            }
        }
    }
    private boolean checkAuthority(LoginRequired loginRequired, Set<String> userRoles) {
        // ADMIN 권한이 필요한 경우
        if (loginRequired.isAdmin()) {
            if (userRoles.contains(AuthorizationType.ROLE_ADMIN.name())) {
                return true;
            }
        }

        // HOST 권한이 필요한 경우
        if (loginRequired.isHost()) {
            if (userRoles.contains(AuthorizationType.ROLE_HOST.name())) {
                return true;
            }
        }

        // USER 권한이 필요한 경우
        if (loginRequired.isUser()) {
            if (userRoles.contains(AuthorizationType.ROLE_USER.name())) {
                return true;
            }
        }

        return false;
    }

    private boolean requiredRole(LoginRequired loginRequired) {
        return loginRequired.isAdmin() || loginRequired.isHost() || loginRequired.isUser();
    }
}
