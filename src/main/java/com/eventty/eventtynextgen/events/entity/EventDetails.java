package com.eventty.eventtynextgen.events.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "event_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_basic_id", nullable = false)
    private EventBasic eventBasic;

    private String content;

    @Column(nullable = false, name = "apply_start_at")
    private LocalDateTime applyStartAt;

    @Column(nullable = false, name = "apply_end_at")
    private LocalDateTime applyEndAt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long views;

    @CreatedDate
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private EventDetails(EventBasic eventBasic, String content, LocalDateTime applyStartAt, LocalDateTime applyEndAt, Long views, LocalDateTime createdAt,
        LocalDateTime updatedAt) {
        this.eventBasic = eventBasic;
        this.content = content;
        this.applyStartAt = applyStartAt;
        this.applyEndAt = applyEndAt;
        this.views = views;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EventDetails of(EventBasic eventBasic, String content, LocalDateTime applyStartAt, LocalDateTime applyEndAt) {
        return EventDetails.builder()
            .eventBasic(eventBasic)
            .content(content)
            .applyStartAt(applyStartAt)
            .applyEndAt(applyEndAt)
            .views(0L)
            .createdAt(null)
            .updatedAt(null)
            .build();
    }
}
