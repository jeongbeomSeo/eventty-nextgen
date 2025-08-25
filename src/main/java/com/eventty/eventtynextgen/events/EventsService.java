package com.eventty.eventtynextgen.events;

import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public interface EventsService {

    void createEvent(@Valid CreateEventArgs args);

    @Builder
    record CreateEventArgs(
        Long hostId,
        String title,
        List<String> imageUrls,
        EventCategoryType category,
        LocalDateTime eventStartAt,
        LocalDateTime eventEndAt,
        EventParticipantLimitPolicyType participantLimitPolicy,
        Integer maxParticipants,
        String location,
        String description,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt
    ) {
    }
}
