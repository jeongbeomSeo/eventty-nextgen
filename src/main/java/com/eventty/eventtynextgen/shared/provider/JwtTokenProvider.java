package com.eventty.eventtynextgen.shared.provider;

import static com.eventty.eventtynextgen.base.exception.enums.AuthErrorType.FAIL_VERIFY_JWT_TOKEN;
import static com.eventty.eventtynextgen.shared.constant.SharedConst.OBJECT_MAPPER;
import static com.eventty.eventtynextgen.shared.utils.sequence.Assertions.notNull;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.Set;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JwtTokenProvider {

    public static final String JWT_SECRET_KEY = "d172e90745bcc237af59f500a4d6acded461842227719b69f493cbf29c6a7acc0cfd00ae2117f3d5be5787427ab390988b23bf0968214595e68c2b0613118af3";
    public static final String JWT_TOKEN_TYPE = "Bearer";
    public static final String JWT_CLAIM_USER_ID_KEY = "userId";
    public static final String API_ALLOW_KEY = "API_Allow";
    public static final String ADMIN_EMAIL_KEY = "AdminEmail";
    public static final String APP_NAME_KEY = "AppName";


    public static SessionTokenInfo createSessionToken(Long userId, Long accessTokenValidityInMS, Long refreshTokenValidityInMS) {
        notNull("userId", userId);

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

    public static AccessTokenPayload extractAccessTokenPayloadIgnoringExpiration(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims == null) {
            throw CustomException.badRequest(FAIL_VERIFY_JWT_TOKEN);
        }

        Long userId = claims.get(JWT_CLAIM_USER_ID_KEY, Long.class);

        return new AccessTokenPayload(userId);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AccessTokenPayload {

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
            log.error("Verify token error. token: {}, msg: {}", token, ex.getMessage());
            return VerifyTokenResult.EXPIRED_TOKEN;
        } catch (UnsupportedJwtException ex) {
            log.error("Verify token error. token: {}, msg: {}", token, ex.getMessage());
            return VerifyTokenResult.UNSUPPORTED_TOKEN;
        } catch (IllegalStateException | MalformedJwtException ex) {
            log.error("Verify token error. token: {}, msg: {}", token, ex.getMessage());
            return VerifyTokenResult.ILLEGAL_STATE_TOKEN;
        } catch (SignatureException ex) {
            log.error("Verify token error. token: {}, msg: {}", token, ex.getMessage());
            return VerifyTokenResult.INVALID_SIGNATURE_TOKEN;
        } catch (Exception ex) {
            log.error("Verify token error. token: {}, msg: {}", token, ex.getMessage());
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
        } catch (Exception e) {
            return null;
        }
    }

    public static CertificationTokenInfo createCertificationToken(String appName, Set<String> apiPermission, long certificationTokenValidityInMS) {
        long now = new Date(System.currentTimeMillis()).getTime();
        Map<String, Object> claims = Map.of(APP_NAME_KEY, appName,
            ADMIN_EMAIL_KEY, "jeongbeom4693@gmail.com",
            API_ALLOW_KEY, apiPermission);

        String certificationToken = Jwts.builder()
            .addClaims(claims)
            .setExpiration(new Date(now + certificationTokenValidityInMS))
            .signWith(getSigningKey())
            .compact();

        return new CertificationTokenInfo(JWT_TOKEN_TYPE, certificationToken);
    }

    public static CertificationTokenPayload extractCertificationTokenPayloadIgnoringExpiration(String certificationToken) {
        Claims claims = parseClaims(certificationToken);

        if (claims == null) {
            throw CustomException.badRequest(FAIL_VERIFY_JWT_TOKEN);
        }

        String appName = claims.get(APP_NAME_KEY, String.class);
        String adminEmail = claims.get(ADMIN_EMAIL_KEY, String.class);

        Object rawApiPermission = claims.get(API_ALLOW_KEY);
        Set<String> apiPermission = OBJECT_MAPPER.convertValue(rawApiPermission, new TypeReference<>() {
        });

        return new CertificationTokenPayload(appName, apiPermission, adminEmail);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CertificationTokenPayload {

        private String appName;
        private Set<String> apiPermission;
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
