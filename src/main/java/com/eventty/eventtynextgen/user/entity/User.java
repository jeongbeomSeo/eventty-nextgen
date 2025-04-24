package com.eventty.eventtynextgen.user.entity;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "email_idx", columnList = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleType userRole;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String birth;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    private LocalDateTime deleteTime;

    // TODO: createAt, ModifyAt 및 추적을 위한 데이터 추가 고려 (abstract class: MappedSuperclass)

    @Builder
    private User(String email, String password, UserRoleType userRole, String name, String phone, String birth) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.name = name;
        this.phone = phone;
        this.birth = birth;
        this.isDeleted = false;
        this.deleteTime = null;
    }

    public static User of(String email, String password, UserRoleType userRole, String name, String phone, String birth) {
        return User.builder()
            .email(email)
            .password(password)
            .userRole(userRole)
            .name(name)
            .phone(phone)
            .birth(birth)
            .build();
    }

    public void updatePersonalInfo(String name, String phone, String birth) {
        this.name = name;
        this.phone = phone;
        this.birth = birth;
    }

    public void updateDeleteStatus(UserStatus status) {
        if (status == UserStatus.ARCHIVE) {
            this.isDeleted = false;
            this.deleteTime = null;
        } else if (status == UserStatus.DELETED) {
            this.isDeleted = true;
            this.deleteTime = LocalDateTime.now();
        }
    }

    public enum UserStatus {
        ARCHIVE,
        DELETED
    }
}
