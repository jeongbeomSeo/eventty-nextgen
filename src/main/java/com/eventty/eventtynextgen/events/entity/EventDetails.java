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
@Table(name = "event_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_basic_id", nullable = false)
    private Long eventBasicId;

    private String content;

    @Column(name = "apply_start_at", nullable = false)
    private LocalDateTime applyStartAt;

    @Column(name = "apply_end_at", nullable = false)
    private LocalDateTime applyEndAt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long views;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private EventDetails(Long eventBasicId, String content, LocalDateTime applyStartAt, LocalDateTime applyEndAt, Long views, boolean isDeleted,
        LocalDateTime deletedAt) {
        this.eventBasicId = eventBasicId;
        this.content = content;
        this.applyStartAt = applyStartAt;
        this.applyEndAt = applyEndAt;
        this.views = views;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }

    public static EventDetails of(Long eventBasicId, String content, LocalDateTime applyStartAt, LocalDateTime applyEndAt) {
        return EventDetails.builder()
            .eventBasicId(eventBasicId)
            .content(content)
            .applyStartAt(applyStartAt)
            .applyEndAt(applyEndAt)
            .views(0L)
            .isDeleted(false)
            .deletedAt(null)
            .build();
    }

    public void updateDeletedStatus(EventDetailsStatus status) {
        if (status == EventDetailsStatus.ACTIVE) {
            this.isDeleted = false;
            this.deletedAt = null;
        } else if (status == EventDetailsStatus.DELETED) {
            this.isDeleted = true;
            this.deletedAt = LocalDateTime.now();
        }
    }

    public enum EventDetailsStatus {
        ACTIVE,
        DELETED
    }
}
