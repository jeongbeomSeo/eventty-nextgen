package com.eventty.eventtynextgen.auth.redis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "email_verification")
public class EmailVerification {

    @Id
    private String email;

    private String code;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long ttl;

    public EmailVerification(String email, String code) {
        this.email = email;
        this.code = code;
        this.ttl = 10L;
    }
}
