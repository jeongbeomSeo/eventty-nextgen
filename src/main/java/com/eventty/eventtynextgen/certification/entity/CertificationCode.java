package com.eventty.eventtynextgen.certification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification_code", indexes = {
    @Index(name = "idx_code", columnList = "code")
})
public class CertificationCode {

    @Id
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    private LocalDateTime expiredAt;

    @Transient
    private final Long ttl = 10L;

    private CertificationCode(String email, String code) {
        this.email = email;
        this.code = code;
        this.expiredAt = LocalDateTime.now().plusMinutes(this.getTtl());
    }

    public static CertificationCode of(String email, String code) {
        return new CertificationCode(email, code);
    }

    public boolean isExpired() {
        return this.expiredAt.isBefore(LocalDateTime.now());
    }
}
