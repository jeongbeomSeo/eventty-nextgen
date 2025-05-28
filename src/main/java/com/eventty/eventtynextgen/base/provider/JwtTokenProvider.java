package com.eventty.eventtynextgen.base.provider;

import com.eventty.eventtynextgen.base.constant.BaseConst;
import com.eventty.eventtynextgen.certification.constant.CertificationConst;
import com.eventty.eventtynextgen.certification.core.Authentication;
import com.eventty.eventtynextgen.certification.core.GrantedAuthority;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final Long accessTokenValidityInMin;
    private final Long refreshTokenValidityInMin;

    public JwtTokenProvider(
        @Value("${key.jwt.secret-key}") String secretKey,
        @Value("${key.jwt.access-token-validity-in-min}") Long accessTokenValidityInMin,
        @Value("${key.jwt.refresh-token-validity-in-min}") Long refreshTokenValidityInMin) {
        this.secretKey = secretKey;
        this.accessTokenValidityInMin = accessTokenValidityInMin * 60 * 1000;
        this.refreshTokenValidityInMin = refreshTokenValidityInMin * 60 * 1000;
    }

        public TokenInfo createToken(Authentication authentication) {
        Assert.isTrue(authentication.isAuthorized(), "Only authorized users can generate a JWT token.");

        UserDetails userDetails = authentication.getUserDetails();

        return this.createTokenInfo(
            userDetails.getUserId(),
            this.convertAuthoritiesToPayload(authentication.getAuthorities()),
            BaseConst.EVENTTY_NAME);
    }

    public TokenInfo createTokenByExpiredToken(String accessToken) {
        AccessTokenPayload payload = this.retrievePayload(accessToken);

        return this.createTokenInfo(payload.getUserId(), payload.getRole(), payload.getAppName());
    }

    private TokenInfo createTokenInfo(Long userId, String role, String appName) {
        long now = new Date(System.currentTimeMillis()).getTime();
        String accessToken = Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("role", role)
            .claim("appName", appName)
            .setExpiration(new Date(now + accessTokenValidityInMin))
            .signWith(this.getSigningKey())
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + refreshTokenValidityInMin))
            .signWith(this.getSigningKey())
            .compact();

        return new TokenInfo(CertificationConst.JWT_TOKEN_TYPE, accessToken, refreshToken);
    }

    private String convertAuthoritiesToPayload(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    }

    public void verifyToken(String token)  throws ExpiredJwtException, UnsupportedJwtException, IllegalStateException, SignatureException {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token);
    }

    public AccessTokenPayload retrievePayload(String accessToken) {
        Claims claims = this.parseClaims(accessToken);

        Long userId = Long.parseLong(claims.getSubject());
        String role = (String) claims.get("role");
        String appName = (String) claims.get("appName");

        return new AccessTokenPayload(userId, role, appName);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(accessToken)
                .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Getter
    public static class AccessTokenPayload {
        private final Long userId;
        private final String role;
        private final String appName;

        private AccessTokenPayload(Long userId, String role, String appName) {
            this.userId = userId;
            this.role = role;
            this.appName = appName;
        }
    }

    @Getter
    public static class TokenInfo {
        private final String tokenType;
        private final String accessToken;
        private final String refreshToken;

        private TokenInfo(String tokenType, String accessToken, String refreshToken) {
            this.tokenType = tokenType;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
