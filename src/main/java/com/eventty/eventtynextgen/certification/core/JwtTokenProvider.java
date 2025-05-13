package com.eventty.eventtynextgen.certification.core;

import com.eventty.eventtynextgen.certification.constant.CertificationConst;
import com.eventty.eventtynextgen.certification.core.userdetails.UserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Builder;
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
        Assert.isTrue(authentication.isAuthenticated(), "Only authenticated users can generate a JWT token.");

        UserDetails userDetails = authentication.getUserDetails();

        long now = new Date(System.currentTimeMillis()).getTime();
        String accessToken = Jwts.builder()
            .setSubject(userDetails.getLoginId())
            .claim("role", userDetails.getUserRole().name())
            .claim("id", userDetails.getUserId())
            .setExpiration(new Date(now + accessTokenValidityInMin))
            .signWith(getSigningKey())
            .compact();

        String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + refreshTokenValidityInMin))
            .signWith(getSigningKey())
            .compact();

        return new TokenInfo(CertificationConst.JWT_TOKEN_TYPE, accessToken, refreshToken);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Getter
    public static class TokenInfo {

        private String tokenType;
        private String accessToken;
        private String refreshToken;

        @Builder
        private TokenInfo(String tokenType, String accessToken, String refreshToken) {
            this.tokenType = tokenType;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
