package com.eventty.eventtynextgen.auth.redis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "email_validation", timeToLive = 600L)   // 10분
public class EmailVerification {

    @Id
    private String authUserId;

    private String code;

    public EmailVerification(String authUserId, String code) {
        this.authUserId = authUserId;
        this.code = code;
    }
}
