package com.eventty.eventtynextgen.base.aspect;

import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
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
        if (loginRequired.requireLogin()) {
            if (!SessionContextHolder.getContext().validate()) {
                throw CustomException.of(HttpStatus.FORBIDDEN, AuthErrorType.LOGIN_REQUIRED_API);
            }

            // API 호출 권한이 필요한 경우
            if (requiredRole(loginRequired)) {
                String userRole = SessionContextHolder.getContext().getRole();

                if (!checkAuthority(loginRequired, userRole)) {
                    // 1개라도 일치하는 권한이 없을 경우
                    throw CustomException.of(HttpStatus.FORBIDDEN, AuthErrorType.AUTH_USER_NOT_AUTHORIZED);
                }
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
