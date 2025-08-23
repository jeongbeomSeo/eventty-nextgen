package com.eventty.eventtynextgen.events;

import com.eventty.eventtynextgen.events.entity.EventBasic;
import com.eventty.eventtynextgen.events.entity.EventDetails;
import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import com.eventty.eventtynextgen.events.service.EventBasicService;
import com.eventty.eventtynextgen.events.service.EventDetailsService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventBasicService eventBasicService;
    private final EventDetailsService eventDetailsService;

    @Override
    public void createEvent(Long hostId, String title, List<String> imageUrls, EventCategoryType categoryType, LocalDateTime eventStartAt, LocalDateTime eventEndAt,
        EventParticipantLimitPolicyType participantLimitPolicyType, Integer maxParticipants, String location, String description, LocalDateTime applyStartAt,
        LocalDateTime applyEndAt) {

        EventBasic eventBasic = this.eventBasicService.saveEventBasic(hostId, title, imageUrls, categoryType, eventStartAt, eventEndAt,
            participantLimitPolicyType, maxParticipants, location);

        EventDetails eventDetails = this.eventDetailsService.saveEventDetails(eventBasic.getId(), description, applyStartAt, applyEndAt);
    }
}
