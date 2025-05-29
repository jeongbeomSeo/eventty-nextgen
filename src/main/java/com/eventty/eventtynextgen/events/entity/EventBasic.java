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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "event_basic")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventBasic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private User host;

    @Column(nullable = false, length = 50)
    private String title;

    private String image;

    @Column(nullable = false, name = "event_start_at")
    private LocalDateTime eventStartAt;

    @Column(nullable = false, name = "event_end_at")
    private LocalDateTime eventEndAt;

    @Column(nullable = false, name = "max_participants")
    private Integer maxParticipants;

    @Column(nullable = false)
    private String location;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private EventCategory category;

    @Column(nullable = false, name = "is_application_active")
    @ColumnDefault("true")
    @Comment("행사 신청 활성화 여부")
    private boolean isApplicationActive;

    @CreatedDate
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false, name = "is_deleted")
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private EventBasic(User host, String title, String image, LocalDateTime eventStartAt, LocalDateTime eventEndAt, Integer maxParticipants, String location,
        EventCategory category, boolean isApplicationActive, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted, LocalDateTime deletedAt) {
        this.host = host;
        this.title = title;
        this.image = image;
        this.eventStartAt = eventStartAt;
        this.eventEndAt = eventEndAt;
        this.maxParticipants = maxParticipants;
        this.location = location;
        this.category = category;
        this.isApplicationActive = isApplicationActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }

    public static EventBasic of(User host, String title, String image, LocalDateTime eventStartAt, LocalDateTime eventEndAt, Integer maxParticipants,
        String location,
        EventCategory category, boolean isApplicationActive) {
        return EventBasic.builder()
            .host(host)
            .title(title)
            .image(image)
            .eventStartAt(eventStartAt)
            .eventEndAt(eventEndAt)
            .maxParticipants(maxParticipants)
            .location(location)
            .category(category)
            .isApplicationActive(isApplicationActive)
            .createdAt(null)
            .updatedAt(null)
            .isDeleted(false)
            .deletedAt(null)
            .build();
    }


    public void updateDeleteStatus(EventStatus status) {
        if (status == EventStatus.ACTIVE) {
            this.isDeleted = false;
            this.deletedAt = null;
        } else if (status == EventStatus.DELETED) {
            this.isDeleted = true;
            this.deletedAt = LocalDateTime.now();
        }
    }

    public enum EventStatus {
        ACTIVE,
        DELETED
    }

    @PreUpdate
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
