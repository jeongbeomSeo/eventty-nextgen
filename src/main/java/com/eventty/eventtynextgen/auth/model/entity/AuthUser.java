package com.eventty.eventtynextgen.auth.model.entity;

import com.eventty.eventtynextgen.auth.model.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "auth")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    private LocalDateTime deleteTime;

    // TODO: createAt, ModifyAt 및 추적을 위한 데이터 추가 고려 (abstract class: MappedSuperclass)

    public AuthUser(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.isDeleted = false;
        this.deleteTime = null;
    }

    public void delete() {
        this.isDeleted = true;
        this.deleteTime = LocalDateTime.now();
    }
}
