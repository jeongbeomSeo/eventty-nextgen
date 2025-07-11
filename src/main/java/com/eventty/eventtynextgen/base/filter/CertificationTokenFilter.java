package com.eventty.eventtynextgen.base.filter;

import static com.eventty.eventtynextgen.base.constant.BaseConst.PATH_MATCHER;
import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_TOKEN_COOKIE_NAME;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.CertificationTokenPayload;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.VerifyTokenResult;
import com.eventty.eventtynextgen.shared.context.CertificationContext;
import com.eventty.eventtynextgen.shared.context.CertificationContextHolder;
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
        "/swagger-ui/**", "/v3/api-docs/**", "/health/**", "/api/*/certification/**"
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

        CertificationTokenPayload payload = JwtTokenProvider.retrieveCertificationToken(token);
        context.updateFromTokenClaims(
            payload.getAppName(),
            payload.getApiPermission(),
            payload.getAdminEmail()
        );
    }
}
