package com.eventty.eventtynextgen.base.filter.certification;

import com.eventty.eventtynextgen.base.enums.ApiNameType;
import com.eventty.eventtynextgen.base.matcher.ApiNamePatternMatcher;
import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties;
import com.eventty.eventtynextgen.shared.context.AuthorizationContext;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

@Order(-1)
@Component
@RequiredArgsConstructor
public class CertificationAuthFilter extends OncePerRequestFilter {

    private final AuthorizationApiProperties authorizationApiProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. URI와 METHOD 가져오기
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();

        // 2. ApiNameType으로 부터 패턴 매칭 후 일치하는 상수 가져오기
        ApiNameType authorizationApiName = ApiNamePatternMatcher.getApiNameByUrlAndMethod(requestUrl, method)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "디버깅용: CertificationAuthFilter의 2번 로직"));

        // 3. properties를 이용하여 권한 가져오기
        String permission = authorizationApiProperties.getPermission(authorizationApiName.name());
        if (permission == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "디버깅용: CertificationAuthFilter의 3번 로직");
        }

        // 4. 권한을 통해서 API 호출 권한 확인
        checkApiPermission(permission, request);

        // 5. 전부 통과시 패스
        doFilter(request, response, filterChain);
    }

    private void checkApiPermission(String permission, HttpServletRequest request) {
        if (!"PASS".equalsIgnoreCase(permission)) {
            AuthorizationContext authorizationContext = AuthorizationContextHolder.getContext();
            if (!authorizationContext.validate()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "디버깅용: CertificationAuthFilter의 4번 로직");
            }
        }

        if ("OPTIONAL".equals(permission)) {
            // TODO - 현재 어떻게 구현해야 할지 감이 안옴. (config 활용?)
        }
    }


}
