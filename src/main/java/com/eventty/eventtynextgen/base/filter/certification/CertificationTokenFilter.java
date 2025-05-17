package com.eventty.eventtynextgen.base.filter.certification;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.JWT_TOKEN_TYPE;
import static com.eventty.eventtynextgen.shared.constant.HttpHeaderConst.*;

import com.eventty.eventtynextgen.certification.core.JwtTokenProvider;
import com.eventty.eventtynextgen.certification.core.JwtTokenProvider.AccessTokenPayload;
import com.eventty.eventtynextgen.shared.context.AuthorizationContext;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(-2)
@RequiredArgsConstructor
@Component
public class CertificationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtAccessToken = this.parseJwtToken(request);

        if (StringUtils.hasText(jwtAccessToken)) {
            try {
                this.jwtTokenProvider.verifyToken(jwtAccessToken);
            } catch (ExpressionException | UnsupportedJwtException | IllegalStateException | SignatureException ex) {
                // TODO - Exception 처리
            }
            AccessTokenPayload payload = this.jwtTokenProvider.retrievePayload(jwtAccessToken);

            AuthorizationContextHolder.getContext().updateContext(payload.getUserId(), payload.getRole());
        }

        filterChain.doFilter(request, response);

        AuthorizationContextHolder.clearContext();
    }

    private String parseJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(jwtToken) && jwtToken.startsWith(JWT_TOKEN_TYPE)) {
            return jwtToken.substring(JWT_TOKEN_TYPE.length() + 1);
        }

        return null;
    }
}
