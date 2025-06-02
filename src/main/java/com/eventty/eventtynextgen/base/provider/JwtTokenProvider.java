package com.eventty.eventtynextgen.base.provider;

import static com.eventty.eventtynextgen.base.constant.BaseConst.ADMIN_EMAIL_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.API_ALLOW_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.APP_NAME_KEY;
import static com.eventty.eventtynextgen.certification.constant.CertificationConst.JWT_TOKEN_TYPE;

import com.eventty.eventtynextgen.base.constant.BaseConst;
import com.eventty.eventtynextgen.base.properties.AuthorizationApiProperties.Permission;
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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
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
    private final Long certificationTokenValidityInMin = 120L;

    public JwtTokenProvider(
        @Value("${key.jwt.secret-key}") String secretKey,
        @Value("${key.jwt.access-token-validity-in-min}") Long accessTokenValidityInMin,
        @Value("${key.jwt.refresh-token-validity-in-min}") Long refreshTokenValidityInMin) {
        this.secretKey = secretKey;
        this.accessTokenValidityInMin = accessTokenValidityInMin * 60 * 1000;
        this.refreshTokenValidityInMin = refreshTokenValidityInMin * 60 * 1000;
    }

    public AccessTokenInfo createAccessToken(Authentication authentication) {
        Assert.isTrue(authentication.isAuthorized(), "Only authorized users can generate a JWT token.");

        return this.createAccessTokenInfo();
    }

    public AccessTokenInfo createAccessTokenByExpiredToken() {
        return this.createAccessTokenInfo();
    }

    private AccessTokenInfo createAccessTokenInfo() {
        long now = new Date(System.currentTimeMillis()).getTime();
        Date accessTokenExpiredAt = new Date(now + accessTokenValidityInMin);
        Date refreshTokenExpiredAt = new Date(now + refreshTokenValidityInMin);
        String accessToken = Jwts.builder()
            .setExpiration(accessTokenExpiredAt)
            .signWith(this.getSigningKey())
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(refreshTokenExpiredAt)
            .signWith(this.getSigningKey())
            .compact();

        return new AccessTokenInfo(JWT_TOKEN_TYPE, accessToken, accessTokenExpiredAt, refreshToken, refreshTokenExpiredAt);
    }

    public void verifyToken(String token)  throws ExpiredJwtException, UnsupportedJwtException, IllegalStateException, SignatureException {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token);
    }

    // TODO: 삭제 예정
    public AccessTokenPayload retrievePayload(String accessToken) {
        Claims claims = this.parseClaims(accessToken);

        Long userId = 1L;
        String role = "USER_ROLE";
        String appName = "client-appname1";

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

    public CertificationTokenInfo createCertificationToken(String appName, Map<String, Permission> apiPermissions) {
        long now = new Date(System.currentTimeMillis()).getTime();
        Map<String, Object> claims = Map.of(APP_NAME_KEY, appName,
            ADMIN_EMAIL_KEY, "jeongbeom4693@gmail.com",
            API_ALLOW_KEY, apiPermissions);

        String certificationToken = Jwts.builder()
            .addClaims(claims)
            .setExpiration(new Date(now + certificationTokenValidityInMin))
            .signWith(this.getSigningKey())
            .compact();

        return new CertificationTokenInfo(JWT_TOKEN_TYPE, certificationToken);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String convertAuthoritiesToPayload(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
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
    public static class CertificationTokenInfo {
        private final String tokenType;
        private final String certificationToken;

        private CertificationTokenInfo(String tokenType, String certificationToken) {
            this.tokenType = tokenType;
            this.certificationToken = certificationToken;
        }
    }

    @Getter
    public static class AccessTokenInfo {
        private final String tokenType;
        private final String accessToken;
        private final Date accessTokenExpiredAt;
        private final String refreshToken;
        private final Date refreshTokenExpiredAt;

        private AccessTokenInfo(String tokenType, String accessToken, Date accessTokenExpiredAt, String refreshToken, Date refreshTokenExpiredAt) {
            this.tokenType = tokenType;
            this.accessToken = accessToken;
            this.accessTokenExpiredAt = accessTokenExpiredAt;
            this.refreshToken = refreshToken;
            this.refreshTokenExpiredAt = refreshTokenExpiredAt;
        }
    }
}
