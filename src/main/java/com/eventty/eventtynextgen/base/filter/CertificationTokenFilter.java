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
        // 인증 관련
        "/api/*/certification/**"
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
            handleToken(token, context);

            filterChain.doFilter(request, response);
        } finally {
            CertificationContextHolder.clearContext();
        }
    }

    private boolean shouldSkip(String uri) {
        // "/api"로 시작하지 않는 URI는 모두 스킵 대상
        if (!uri.startsWith("/api")) {
            return true;
        }

        return SKIP_PATTERNS.stream()
            .anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
    }

    private void handleToken(String token, CertificationContext context) {
        if (!StringUtils.hasText(token)) {
            context.updateTokenParsingFailureReason("NO_TOKEN");
        }
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
