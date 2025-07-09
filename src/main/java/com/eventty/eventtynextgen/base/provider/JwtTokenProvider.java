package com.eventty.eventtynextgen.base.provider;

import static com.eventty.eventtynextgen.base.constant.BaseConst.ADMIN_EMAIL_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.API_ALLOW_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.APP_NAME_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.JWT_CLAIM_USER_ID_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.JWT_SECRET_KEY;
import static com.eventty.eventtynextgen.base.constant.BaseConst.JWT_TOKEN_TYPE;

import com.eventty.eventtynextgen.config.properties.CertificationApiProperties.Permission;
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
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Nested;

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

    public static AccessTokenPayload retrievePayload(String accessToken) {
        Claims claims = parseClaims(accessToken);

        Long userId = claims.get(JWT_CLAIM_USER_ID_KEY, Long.class);

        return new AccessTokenPayload(userId);
    }

    private static Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(accessToken)
                .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public static CertificationTokenInfo createCertificationToken(String appName, Map<String, Permission> apiPermissions, long certificationTokenValidityInMS) {
        long now = new Date(System.currentTimeMillis()).getTime();
        Map<String, Object> claims = Map.of(APP_NAME_KEY, appName,
            ADMIN_EMAIL_KEY, "jeongbeom4693@gmail.com",
            API_ALLOW_KEY, apiPermissions);

        String certificationToken = Jwts.builder()
            .addClaims(claims)
            .setExpiration(new Date(now + certificationTokenValidityInMS))
            .signWith(getSigningKey())
            .compact();

        return new CertificationTokenInfo(JWT_TOKEN_TYPE, certificationToken);
    }

    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Getter
    public static class AccessTokenPayload {

        private final Long userId;

        private AccessTokenPayload(Long userId) {
            this.userId = userId;
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
    public static class SessionTokenInfo {

        private final String tokenType;
        private final String accessToken;
        private final Date accessTokenExpiredAt;
        private final String refreshToken;
        private final Date refreshTokenExpiredAt;

        private SessionTokenInfo(String tokenType, String accessToken, Date accessTokenExpiredAt, String refreshToken, Date refreshTokenExpiredAt) {
            this.tokenType = tokenType;
            this.accessToken = accessToken;
            this.accessTokenExpiredAt = accessTokenExpiredAt;
            this.refreshToken = refreshToken;
            this.refreshTokenExpiredAt = refreshTokenExpiredAt;
        }
    }
}
