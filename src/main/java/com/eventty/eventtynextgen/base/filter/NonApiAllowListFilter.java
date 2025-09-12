package com.eventty.eventtynextgen.base.filter;

import static com.eventty.eventtynextgen.shared.constant.SharedConst.PATH_MATCHER;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import com.eventty.eventtynextgen.shared.utils.LoggerUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Order(-4)
@RequiredArgsConstructor
@Component
public class NonApiAllowListFilter extends OncePerRequestFilter {

    private final ResponseUtils responseUtils;

    private static final List<String> SKIP_PATTERNS = List.of(
        // API
        "/api/**",
        // API 문서
        "/swagger-ui/**", "/v3/api-docs/**",
        // 모니터링 및 헬스체크
        "/health/**", "/actuator/**",
        // 정적 리소스
        "/css/**", "/js/**", "/images/**", "/webjars/**", "/fonts/**", "/resources/**", "/static/**",
        // 오류 페이지
        "/error/**",
        // 크롬 개발자 도구 자동 요청이나 인증 경로
        "/.well-known/**",
        // 기타 리소스
        "/favicon.ico", "/robots.txt"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 예외 URL에 해당하는지 확인
        String uri = request.getRequestURI();

        if (shouldSkip(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 예외 URL이 아닐 경우 404 응답
        CustomException customException = CustomException.of(HttpStatus.FORBIDDEN, CertificationErrorType.ACCESS_DENIED, "URI: " + uri);
        writeErrorResponse(customException, response);
    }

    private boolean shouldSkip(String uri) {
        return SKIP_PATTERNS.stream()
            .anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
    }

    private void writeErrorResponse(CustomException customException, HttpServletResponse response) {
        LoggerUtils.debug(customException);

        responseUtils.writeErrorResponseToResponse(response, customException);
    }
}
