package com.eventty.eventtynextgen.events.request;

import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record EventsCreateEventRequestCommand (
    @Schema(description = "행사 제목")
    @NotBlank
    String title,
    @Schema(description = "행사 이미지", nullable = true)
    List<String> imageUrls,
    @Schema(description = "카테고리")
    @NotNull
    EventCategoryType category,
    @Schema(description = "행사 시작 시간")
    @NotNull
    LocalDateTime eventStartAt,
    @Schema(description = "행사 종료 시간")
    @NotNull
    LocalDateTime eventEndAt,
    @Schema(description = "참가 인원 정책")
    @NotNull
    EventParticipantLimitPolicyType participantLimitPolicy,
    @Schema(description = "최대 참가 인원", nullable = true)
    Integer maxParticipants,
    @Schema(description = "행사 주최지")
    @NotBlank
    String location,
    @Schema(description = "행사 상세 설명")
    String description,
    @Schema(description = "예약 시작 일자")
    @NotNull
    LocalDateTime applyStartAt,
    @Schema(description = "예약 종료 일자")
    @NotNull
    LocalDateTime applyEndAt
){
}
