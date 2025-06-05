package com.eventty.eventtynextgen.base.filter.certification;

import static com.eventty.eventtynextgen.base.constant.BaseConst.JWT_TOKEN_TYPE;
import static com.eventty.eventtynextgen.shared.constant.HttpHeaderConst.*;

import com.eventty.eventtynextgen.base.provider.JwtTokenProvider;
import com.eventty.eventtynextgen.base.provider.JwtTokenProvider.AccessTokenPayload;
import com.eventty.eventtynextgen.base.utils.ResponseUtils;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import com.eventty.eventtynextgen.shared.exception.enums.AuthErrorType;
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

@Slf4j
@Order(-2)
@RequiredArgsConstructor
@Component
public class CertificationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtAccessToken = this.parseJwtToken(request);

        if (StringUtils.hasText(jwtAccessToken)) {
            try {

                JwtTokenProvider.verifyToken(jwtAccessToken);

                AccessTokenPayload payload = JwtTokenProvider.retrievePayload(jwtAccessToken);

                AuthorizationContextHolder.getContext().updateContext(payload.getUserId());

                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException ex) {
                log.error("http-status={} code={} msg={} detail={}",
                    HttpStatus.BAD_REQUEST.value(),
                    AuthErrorType.JWT_TOKEN_EXPIRED.getCode(),
                    AuthErrorType.JWT_TOKEN_EXPIRED.getMsg(),
                    ex.getMessage());
                ResponseUtils.writeErrorResponseToResponse(response, HttpStatus.BAD_REQUEST, ex, AuthErrorType.JWT_TOKEN_EXPIRED);
            } catch (Throwable ex) {
                log.error("http-status={} code={} msg={} detail={}",
                    HttpStatus.UNAUTHORIZED.value(),
                    AuthErrorType.FAILED_TOKEN_VERIFIED.getCode(),
                    AuthErrorType.FAILED_TOKEN_VERIFIED.getMsg(),
                    ex.getMessage());
                ResponseUtils.writeErrorResponseToResponse(response, HttpStatus.UNAUTHORIZED, ex, AuthErrorType.FAILED_TOKEN_VERIFIED);
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
