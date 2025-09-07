package com.eventty.eventtynextgen.base.filter;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;
import static com.eventty.eventtynextgen.shared.constant.SharedConst.PATH_MATCHER;

import com.eventty.eventtynextgen.shared.context.CertificationContext;
import com.eventty.eventtynextgen.shared.context.CertificationContextHolder;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.CertificationTokenPayload;
import com.eventty.eventtynextgen.shared.provider.JwtTokenProvider.VerifyTokenResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Order(-3)
@RequiredArgsConstructor
@Component
public class CertificationTokenFilter extends OncePerRequestFilter {

    private static final List<String> SKIP_PATTERNS = List.of(
        // API 문서
        "/swagger-ui/**", "/v3/api-docs/**",
        // 모니터링 및 헬스체크
        "/health/**", "/actuator/**",
        // 인증 관련
        "/api/*/certification/**",
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
        CertificationContext context = CertificationContextHolder.getContext();
        try {
            // 1. 예외 URL에 해당하는지 확인
            String uri = request.getRequestURI();

            if (shouldSkip(uri)) {
                context.markCertificateAsSkipped();
                filterChain.doFilter(request, response);
                return;
            }

            // 2. 토큰이 존재할 경우 파싱
            String token = request.getHeader(CERTIFICATION_TOKEN_COOKIE_NAME);
            if (StringUtils.hasText(token)) {
                handleToken(token, context);
            }

            filterChain.doFilter(request, response);
        } finally {
            CertificationContextHolder.clearContext();
        }
    }

    private boolean shouldSkip(String uri) {
        return SKIP_PATTERNS.stream()
            .anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
    }

    private void handleToken(String token, CertificationContext context) {
        VerifyTokenResult result = JwtTokenProvider.verifyToken(token);
        if (result != VerifyTokenResult.VERIFIED_TOKEN) {
            context.updateTokenParsingFailureReason(result.name());
            return;
        }

        CertificationTokenPayload payload = JwtTokenProvider.extractCertificationTokenPayloadIgnoringExpiration(token);
        context.updateFromTokenClaims(
            payload.getAppName(),
            payload.getApiPermission(),
            payload.getAdminEmail()
        );
    }
}
