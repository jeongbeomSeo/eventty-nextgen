package com.eventty.eventtynextgen.certification.authuser.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_user", indexes =
    @Index(name = "session_id_idx", columnList = "session_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthUser {

    @Id
    private Long userId;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    private AuthUser(Long userId, String userRole, String sessionId, LocalDateTime expiredAt) {
        this.userId = userId;
        this.userRole = userRole;
        this.sessionId = sessionId;
        this.expiredAt = expiredAt;
    }

    public static AuthUser of(Long userId, String userRole, String sessionId, LocalDateTime expiredAt) {
        return AuthUser.builder()
            .userId(userId)
            .userRole(userRole)
            .sessionId(sessionId)
            .expiredAt(expiredAt)
            .build();
    }

    public void updateAuthUserInfo(String userRole, String sessionId, LocalDateTime expiredAt) {
        this.userRole = userRole;
        this.sessionId = sessionId;
        this.expiredAt = expiredAt;
    }
}
