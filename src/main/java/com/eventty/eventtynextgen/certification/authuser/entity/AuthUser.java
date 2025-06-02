package com.eventty.eventtynextgen.certification.authuser.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
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

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
}
