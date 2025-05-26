package com.eventty.eventtynextgen.base.filter.certification;

import com.eventty.eventtynextgen.base.enums.ApiName;
import com.eventty.eventtynextgen.base.matcher.ApiNamePatternMatcher;
import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties;
import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import com.eventty.eventtynextgen.shared.context.AuthorizationContext;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Order(-1)
@Component
@RequiredArgsConstructor
public class CertificationAuthFilter extends OncePerRequestFilter {

    private final AuthorizationApiProperties authorizationApiProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. URI와 METHOD 가져오기
        String requestUrl = request.getRequestURI();

        try {
            // 2. ApiNameType으로 부터 패턴 매칭 후 일치하는 상수 가져오기
            ApiName authorizationApiName = ApiNamePatternMatcher.getApiName(requestUrl)
                .orElseThrow(() -> CustomException.badRequest(CertificationErrorType.NOT_FOUND_API_NAME_TYPE, String.format("The URI (%s) is not a registered API path. Please contact the administrator.", requestUrl)));

            // 3. properties를 이용하여 권한 가져오기
            String appName = AuthorizationContextHolder.getContext().getAppName();
            String permission = authorizationApiProperties.getPermission(appName, authorizationApiName);
            if (permission == null) {
                throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, CertificationErrorType.NOT_FOUND_AUTHORIZATION_API_PROPERTY, String.format("authorizationApiName.name: %s", authorizationApiName.name()));
            }

            // 4. 권한을 통해서 API 호출 권한 확인
            checkApiPermission(permission);

            // 5. 전부 통과시 패스
            doFilter(request, response, filterChain);
        } catch (CustomException ex) {
            log.error("http-status={} code={} msg={} detail={}",
                ex.getHttpStatus().value(),
                ex.getErrorType().getCode(),
                ex.getErrorType().getMsg(),
                ex.getDetail());
            ResponseUtils.writeErrorResponseToResponse(response, ex);
        } catch (Throwable ex) {
            log.error("http-status={} code={} msg={} detail={}",
                HttpStatus.FORBIDDEN.value(),
                CertificationErrorType.UNKNOWN_EXCEPTION.getCode(),
                CertificationErrorType.UNKNOWN_EXCEPTION.getMsg(),
                ex.getMessage());
            ResponseUtils.writeErrorResponseToResponse(response, HttpStatus.FORBIDDEN, ex, CertificationErrorType.UNKNOWN_EXCEPTION);
        }
    }

    private void checkApiPermission(String permission) {
        if ("LOGIN".equalsIgnoreCase(permission)) {
            AuthorizationContext authorizationContext = AuthorizationContextHolder.getContext();
            if (!authorizationContext.validate()) {
                throw CustomException.of(HttpStatus.FORBIDDEN, CertificationErrorType.AUTH_USER_NOT_AUTHORIZED);
            }
        }
    }
}
