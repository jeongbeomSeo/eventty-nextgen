package com.eventty.eventtynextgen.base.filter.certification;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.JWT_TOKEN_TYPE;
import static com.eventty.eventtynextgen.shared.constant.HttpHeaderConst.*;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.AccessTokenPayload;
import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import io.jsonwebtoken.ExpiredJwtException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
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

                AccessTokenPayload payload = this.jwtTokenProvider.retrievePayload(jwtAccessToken);

                AuthorizationContextHolder.getContext().updateContext(payload.getUserId(), payload.getRole());

                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException ex) {
                log.error("http-status={} code={} msg={} detail={}",
                    HttpStatus.BAD_REQUEST.value(),
                    CertificationErrorType.JWT_TOKEN_EXPIRED.getCode(),
                    CertificationErrorType.JWT_TOKEN_EXPIRED.getMsg(),
                    ex.getMessage());
                ResponseUtils.writeErrorResponseToResponse(response, HttpStatus.BAD_REQUEST, ex, CertificationErrorType.JWT_TOKEN_EXPIRED);
            } catch (Throwable ex) {
                log.error("http-status={} code={} msg={} detail={}",
                    HttpStatus.UNAUTHORIZED.value(),
                    CertificationErrorType.FAILED_TOKEN_VERIFIED.getCode(),
                    CertificationErrorType.FAILED_TOKEN_VERIFIED.getMsg(),
                    ex.getMessage());
                ResponseUtils.writeErrorResponseToResponse(response, HttpStatus.UNAUTHORIZED, ex, CertificationErrorType.FAILED_TOKEN_VERIFIED);
            } finally {
                AuthorizationContextHolder.clearContext();
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String parseJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(jwtToken) && jwtToken.startsWith(JWT_TOKEN_TYPE)) {
            return jwtToken.substring(JWT_TOKEN_TYPE.length() + 1);
        }

        return null;
    }
}
