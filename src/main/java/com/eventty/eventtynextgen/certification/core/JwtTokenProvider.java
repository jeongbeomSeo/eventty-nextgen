package com.eventty.eventtynextgen.certification.core;

import com.eventty.eventtynextgen.certification.constant.CertificationConst;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import com.eventty.eventtynextgen.certification.refreshtoken.RefreshTokenService;
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
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final Long accessTokenValidityInMin;
    private final Long refreshTokenValidityInMin;

    private final RefreshTokenService refreshTokenService;

    public JwtTokenProvider(
        @Value("${key.jwt.secret-key}") String secretKey,
        @Value("${key.jwt.access-token-validity-in-min}") Long accessTokenValidityInMin,
        @Value("${key.jwt.refresh-token-validity-in-min}") Long refreshTokenValidityInMin,
        RefreshTokenService refreshTokenService) {
        this.secretKey = secretKey;
        this.accessTokenValidityInMin = accessTokenValidityInMin * 60 * 1000;
        this.refreshTokenValidityInMin = refreshTokenValidityInMin * 60 * 1000;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public TokenInfo createToken(Authentication authentication) {
        Assert.isTrue(authentication.isAuthenticated(), "Only authenticated users can generate a JWT token.");

        UserDetails userDetails = authentication.getUserDetails();

        long now = new Date(System.currentTimeMillis()).getTime();
        String accessToken = Jwts.builder()
            .setSubject(String.valueOf(userDetails.getUserId()))
            .claim("role", this.convertAuthoritiesToPayload(authentication.getAuthorities()))
            .setExpiration(new Date(now + accessTokenValidityInMin))
            .signWith(this.getSigningKey())
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + refreshTokenValidityInMin))
            .signWith(this.getSigningKey())
            .compact();

        refreshTokenService.saveOrUpdate(refreshToken, userDetails.getUserId());

        return new TokenInfo(CertificationConst.JWT_TOKEN_TYPE, accessToken, refreshToken);
    }

    private String convertAuthoritiesToPayload(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    }

    public void verifyToken(String token) throws ExpiredJwtException, UnsupportedJwtException, IllegalStateException, SignatureException {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token);
    }

    public AccessTokenPayload retrievePayload(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

        Long userId = Long.parseLong(claims.getSubject());
        String role = (String) claims.get("role");

        return new AccessTokenPayload(userId, role);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Getter
    public static class AccessTokenPayload {

        private Long userId;
        private String role;

        public AccessTokenPayload(Long userId, String role) {
            this.userId = userId;
            this.role = role;
        }
    }

    @Getter
    public static class TokenInfo {

        private String tokenType;
        private String accessToken;
        private String refreshToken;

        private TokenInfo(String tokenType, String accessToken, String refreshToken) {
            this.tokenType = tokenType;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
