package com.eventty.eventtynextgen.user.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private Long authUserId;

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

    public User(Long authUserId, String name, String phone, String birth) {
        this.authUserId = authUserId;
        this.name = name;
        this.phone = phone;
        this.birth = birth;
        this.isDeleted = false;
        this.deleteTime = null;
    }

    public void update(String name, String phone, String birth) {
        this.name = name;
        this.phone = phone;
        this.birth = birth;
    }

    public void delete() {
        this.isDeleted = true;
        this.deleteTime = LocalDateTime.now();
    }
}
