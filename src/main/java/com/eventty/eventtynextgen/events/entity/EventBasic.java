package com.eventty.eventtynextgen.events.entity;

import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import com.eventty.eventtynextgen.shared.converter.ListToJsonConverter;
import com.eventty.eventtynextgen.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;
import org.hibernate.usertype.UserType;

@Entity
@Table(name = "event_basic")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventBasic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(nullable = false, length = 50)
    private String title;

    @Comment("상품 이미지 URL 리스트")
    @Convert(converter = ListToJsonConverter.class)
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategoryType category;

    @Comment("행사 시작 시간")
    @Column(name = "event_start_at", nullable = false)
    private LocalDateTime eventStartAt;

    @Comment("행사 종료 시간")
    @Column(name = "event_end_at", nullable = false)
    private LocalDateTime eventEndAt;

    @Comment("참가 인원 정책")
    @Enumerated(EnumType.STRING)
    @Column(name = "participant_limit_policy", nullable = false)
    private EventParticipantLimitPolicyType participantLimitPolicy;

    @Comment("최대 참가 인원")
    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Comment("행사 주최지")
    @Column(nullable = false)
    private String location;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private EventBasic(Long hostId, String title, List<String> imageUrls, EventCategoryType category, LocalDateTime eventStartAt, LocalDateTime eventEndAt,
        EventParticipantLimitPolicyType participantLimitPolicy, Integer maxParticipants, String location, boolean isDeleted,
        LocalDateTime deletedAt) {
        this.hostId = hostId;
        this.title = title;
        this.imageUrls = imageUrls;
        this.category = category;
        this.eventStartAt = eventStartAt;
        this.eventEndAt = eventEndAt;
        this.participantLimitPolicy = participantLimitPolicy;
        this.maxParticipants = maxParticipants;
        this.location = location;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }

    public static EventBasic of(Long hostId, String title, List<String> imageUrls, EventCategoryType category, LocalDateTime eventStartAt, LocalDateTime eventEndAt,
        EventParticipantLimitPolicyType participantLimitPolicy, Integer maxParticipants, String location) {
        return EventBasic.builder()
            .hostId(hostId)
            .title(title)
            .imageUrls(imageUrls)
            .category(category)
            .eventStartAt(eventStartAt)
            .eventEndAt(eventEndAt)
            .participantLimitPolicy(participantLimitPolicy)
            .maxParticipants(maxParticipants)
            .location(location)
            .isDeleted(false)
            .deletedAt(null)
            .build();
    }

    public void updateDeletedStatus(EventStatus status) {
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
}
