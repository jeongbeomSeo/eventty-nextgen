package com.eventty.eventtynextgen.base.aspect;

import com.eventty.eventtynextgen.base.annotation.PreAuthorize;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
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
public class PreAuthorizeAspect {

    @Before("@within(preAuthorize) || @annotation(preAuthorize)")
    public void checkAuthority(JoinPoint joinPoint, PreAuthorize preAuthorize) {

        Set<String> userRoles = Arrays.stream(
                AuthorizationContextHolder.getContext().getRole().split(","))
            .map(String::trim)
            .map(String::toUpperCase)
            .collect(Collectors.toSet());

        boolean authorized = Arrays.stream(preAuthorize.value())
            .map(Enum::name)
            .anyMatch(userRoles::contains);

        if (!authorized) {
            throw CustomException.of(HttpStatus.FORBIDDEN, CertificationErrorType.AUTH_USER_NOT_ACTIVE);
        }
    }
}
