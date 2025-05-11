package com.eventty.eventtynextgen.certification.certificationcode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification_code", indexes = {
    @Index(name = "idx_code", columnList = "email, code")
})
public class CertificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    private LocalDateTime expiredAt;

    private boolean isExpired;

    @Builder
    private CertificationCode(String email, String code, int ttl) {
        this.email = email;
        this.code = code;
        this.expiredAt = LocalDateTime.now().plusMinutes(ttl);
        this.isExpired = false;
    }

    /**
     * 엔티티를 만들어 주는 팩토리 메서드 함수입니다.
     *
     * @param email 사용자 이메일
     * @param code 인증 코드
     * @param ttl Time to live (분단위)
     * @return 엔티티 객체
     */
    public static CertificationCode of(String email, String code, int ttl) {
        return CertificationCode.builder()
            .email(email)
            .code(code)
            .ttl(ttl)
            .build();
    }

    public void setExpired() {
        this.isExpired = true;
    }
}
