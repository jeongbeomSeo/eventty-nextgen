package com.eventty.eventtynextgen.user.redis.entity;

import jakarta.persistence.Id;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "email_verification")
public class EmailVerification {

    @Id
    private String id;

    @Indexed
    private String email;

    private String code;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long ttl;

    private EmailVerification(String email, String code) {
        this.email = email;
        this.code = code;
        this.ttl = 10L;
    }

    public static EmailVerification createEntity(String email, String code) {
        return new EmailVerification(email, code);
    }
}
