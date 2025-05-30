package com.eventty.eventtynextgen.events.entity;

import com.eventty.eventtynextgen.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "event_interest")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventInterest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_basic_id", nullable = false)
    private Long eventBasicId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private EventInterest(Long eventBasicId, Long userId, boolean isDeleted, LocalDateTime deletedAt) {
        this.eventBasicId = eventBasicId;
        this.userId = userId;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }

    public static EventInterest of(Long eventBasicId, Long userId) {
        return EventInterest.builder()
            .eventBasicId(eventBasicId)
            .userId(userId)
            .isDeleted(false)
            .deletedAt(null)
            .build();
    }

    private void updateDeletedStatus(EventInterestStatus status) {
        if (status == EventInterestStatus.ACTIVE) {
            this.isDeleted = false;
            this.deletedAt = null;
        } else if (status == EventInterestStatus.DELETED) {
            this.isDeleted = true;
            this.deletedAt = LocalDateTime.now();
        }
    }

    public enum EventInterestStatus {
        ACTIVE,
        DELETED
    }
}
