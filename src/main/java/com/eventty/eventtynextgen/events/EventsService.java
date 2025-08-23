package com.eventty.eventtynextgen.events;

import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EventsService {

    void createEvent(Long hostId, String title, List<String> imageUrls, EventCategoryType categoryType, LocalDateTime eventStartAt, LocalDateTime eventEndAt,
        EventParticipantLimitPolicyType participantLimitPolicyType, Integer maxParticipants, String location, String description, LocalDateTime applyStartAt,
        LocalDateTime applyEndAt);
}
