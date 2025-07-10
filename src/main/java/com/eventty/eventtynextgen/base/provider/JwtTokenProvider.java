package com.eventty.eventtynextgen.base.provider;

import static com.eventty.eventtynextgen.base.constant.BaseConst.ADMIN_EMAIL_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.API_ALLOW_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.APP_NAME_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.JWT_CLAIM_USER_ID_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.JWT_SECRET_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.JWT_TOKEN_TYPE;
import static com.eventty.eventtynextgen.base.constant.BaseConst.OBJECT_MAPPER;

import com.eventty.eventtynextgen.config.properties.CertificationApiProperties.Permission;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JwtTokenProvider {

    public static SessionTokenInfo createSessionToken(Long userId, Long accessTokenValidityInMS, Long refreshTokenValidityInMS) {
        long now = new Date(System.currentTimeMillis()).getTime();
        Date accessTokenExpiredAt = new Date(now + accessTokenValidityInMS);
        Date refreshTokenExpiredAt = new Date(now + refreshTokenValidityInMS);

        String accessToken = Jwts.builder()
            .claim(JWT_CLAIM_USER_ID_KEY, userId)
            .setExpiration(accessTokenExpiredAt)
            .signWith(getSigningKey())
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(refreshTokenExpiredAt)
            .signWith(getSigningKey())
            .compact();

        return new SessionTokenInfo(JWT_TOKEN_TYPE, accessToken, accessTokenExpiredAt, refreshToken, refreshTokenExpiredAt);
    }

    public static SessionTokenPayload retrieveSessionTokenPayload(String sessionToken) {
        Claims claims = parseClaims(sessionToken);

        Long userId = claims.get(JWT_CLAIM_USER_ID_KEY, Long.class);

        return new SessionTokenPayload(userId);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SessionTokenPayload {
        private Long userId;
    }

    public static VerifyTokenResult verifyToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);

            return VerifyTokenResult.VERIFIED_TOKEN;
        } catch (ExpiredJwtException ex) {
            return VerifyTokenResult.EXPIRED_TOKEN;
        } catch (UnsupportedJwtException ex) {
            return VerifyTokenResult.UNSUPPORTED_TOKEN;
        } catch (IllegalStateException | MalformedJwtException ex) {
            return VerifyTokenResult.ILLEGAL_STATE_TOKEN;
        } catch (SignatureException ex) {
            return VerifyTokenResult.INVALID_SIGNATURE_TOKEN;
        } catch (Exception ex) {
            log.error("Token 검증 과정에서 예측하지 못한 예외가 발생했습니다.", ex);
            return VerifyTokenResult.UNKNOWN_ERROR;
        }
    }

    public enum VerifyTokenResult {
        EXPIRED_TOKEN,
        UNSUPPORTED_TOKEN,
        ILLEGAL_STATE_TOKEN,
        INVALID_SIGNATURE_TOKEN,
        UNKNOWN_ERROR,
        VERIFIED_TOKEN
    }

    private static Claims parseClaims(String jwtToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(jwtToken)
                .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public static CertificationTokenInfo createCertificationToken(String appName, Map<String, Permission> apiPermissionMap, long certificationTokenValidityInMS) {
        long now = new Date(System.currentTimeMillis()).getTime();
        Map<String, Object> claims = Map.of(APP_NAME_KEY, appName,
            ADMIN_EMAIL_KEY, "jeongbeom4693@gmail.com",
            API_ALLOW_KEY, apiPermissionMap);

        String certificationToken = Jwts.builder()
            .addClaims(claims)
            .setExpiration(new Date(now + certificationTokenValidityInMS))
            .signWith(getSigningKey())
            .compact();

        return new CertificationTokenInfo(JWT_TOKEN_TYPE, certificationToken);
    }

    public static CertificationTokenPayload retrieveCertificationToken(String certificationToken) {
        Claims claims = parseClaims(certificationToken);

        String appName = claims.get(APP_NAME_KEY, String.class);
        String adminEmail = claims.get(ADMIN_EMAIL_KEY, String.class);

        Object rawApiPermission = claims.get(API_ALLOW_KEY);
        Map<String, Permission> apiPermissionMap = OBJECT_MAPPER.convertValue(rawApiPermission, new TypeReference<>() {});

        return new CertificationTokenPayload(appName, apiPermissionMap, adminEmail);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CertificationTokenPayload {
        private String appName;
        private Map<String, Permission> apiPermissionMap;
        private String adminEmail;
    }


    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CertificationTokenInfo {
        private final String tokenType;
        private final String certificationToken;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SessionTokenInfo {
        private final String tokenType;
        private final String accessToken;
        private final Date accessTokenExpiredAt;
        private final String refreshToken;
        private final Date refreshTokenExpiredAt;
    }
}
