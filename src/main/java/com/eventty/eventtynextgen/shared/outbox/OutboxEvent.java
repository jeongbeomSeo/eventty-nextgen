package com.eventty.eventtynextgen.shared.outbox;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "outbox_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timeStamp;

    @Column(name = "attemps", nullable = false)
    @ColumnDefault("0")
    private Integer attemps;

    @Column(name = "max_attemps", nullable = false)
    private Integer maxAttemps;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("READY_TO_PUBLISH")
    private Status status;

    public enum Status {
        READY_TO_PUBLISH,
        SUCCESS,
        FAILED
    }

    @Builder
    private OutboxEvent(String aggregateType, String aggregateId, String eventType, String payload, LocalDateTime timeStamp, Status status) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.timeStamp = timeStamp;
        this.status = status;
    }

    public static OutboxEvent of(String aggregateType, String aggregateId, String eventType, String payload) {
        return OutboxEvent.builder()
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .payload(payload)
            .timeStamp(LocalDateTime.now())
            .status(Status.READY_TO_PUBLISH)
            .build();
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}
