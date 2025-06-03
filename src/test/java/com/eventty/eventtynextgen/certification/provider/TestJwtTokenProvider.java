package com.eventty.eventtynextgen.certification.provider;

import com.eventty.eventtynextgen.certification.authorization.enums.AuthorizationType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class TestJwtTokenProvider {

    private static final KeyGenerator keyGen;

    private static final Long VALID_TOKEN_VALIDITY_IN_MIN = (long) (60 * 60 * 1000);
    private static final Long INVALID_TOKEN_VALIDITY_IN_MIN = (long) (-60 * 60 * 1000);

    private final String secretKey;

    static {
        try {
            keyGen = KeyGenerator.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("KeyGenerator를 초기화하는 과정에서 예외가 발생했습니다. msg: " + e.getMessage());
            throw new RuntimeException(e);
        }
        keyGen.init(256); // 키 길이를 256비트로 설정
    }

    private TestJwtTokenProvider (String secretKey) {
        this.secretKey = secretKey;
    }

    public static TestJwtTokenProvider of(String secretKey) {
        return new TestJwtTokenProvider(secretKey);
    }

    public String createExpiredAccessToken() {
        long now = new Date(System.currentTimeMillis()).getTime();
        return Jwts.builder()
            .setExpiration(new Date(now + INVALID_TOKEN_VALIDITY_IN_MIN))
            .signWith(this.getSigningKey())
            .compact();
    }

    public String createValidRefreshToken() {
        long now = new Date(System.currentTimeMillis()).getTime();
        return Jwts.builder()
            .setExpiration(new Date(now + VALID_TOKEN_VALIDITY_IN_MIN))
            .signWith(this.getSigningKey())
            .compact();
    }

    public String createDifferentRefreshToken() {
        long now = new Date(System.currentTimeMillis()).getTime();
        return Jwts.builder()
            .setExpiration(new Date(now + VALID_TOKEN_VALIDITY_IN_MIN * 60))
            .signWith(this.getSigningKey())
            .compact();
    }

    public String createExpiredRefreshToken() {
        long now = new Date(System.currentTimeMillis()).getTime();
        return Jwts.builder()
            .setExpiration(new Date(now + INVALID_TOKEN_VALIDITY_IN_MIN))
            .signWith(this.getSigningKey())
            .compact();
    }

    public String createRefreshTokenWithInvalidSignature() {
        long now = new Date(System.currentTimeMillis()).getTime();
        return Jwts.builder()
            .setExpiration(new Date(now + INVALID_TOKEN_VALIDITY_IN_MIN))
            .signWith(this.getInvalidSigningKey())
            .compact();
    }

    public String createIllegalRefreshToken() {
        return "invalid.token.format";
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getInvalidSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public record TokenInfo(String tokenType, String accessToken, String refreshToken) { }
}
