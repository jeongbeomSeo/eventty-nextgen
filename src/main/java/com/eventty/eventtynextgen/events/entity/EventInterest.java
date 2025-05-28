package com.eventty.eventtynextgen.events.entity;

import com.eventty.eventtynextgen.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "event_interest")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventBasic eventBasic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_canceled", nullable = false)
    @ColumnDefault("false")
    private boolean isCanceled;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Builder
    private EventInterest(EventBasic eventBasic, User user, LocalDateTime createdAt, boolean isCanceled, LocalDateTime canceledAt) {
        this.eventBasic = eventBasic;
        this.user = user;
        this.createdAt = createdAt;
        this.isCanceled = isCanceled;
        this.canceledAt = canceledAt;
    }

    public static EventInterest of(EventBasic eventBasic, User user) {
        return EventInterest.builder()
            .eventBasic(eventBasic)
            .user(user)
            .createdAt(null)
            .isCanceled(false)
            .canceledAt(null)
            .build();
    }

    private void updateCanceledStatus(EventInterestStatus status) {
        if (status == EventInterestStatus.ACTIVE) {
            this.isCanceled = false;
            this.canceledAt = null;
        } else if (status == EventInterestStatus.DELETED) {
            this.isCanceled = true;
            this.canceledAt = LocalDateTime.now();
        }
    }

    public enum EventInterestStatus {
        ACTIVE,
        DELETED
    }
}
