package com.eventty.eventtynextgen.events;

import com.eventty.eventtynextgen.events.entity.EventBasic;
import com.eventty.eventtynextgen.events.entity.EventDetails;
import com.eventty.eventtynextgen.events.service.EventBasicService;
import com.eventty.eventtynextgen.events.service.EventBasicService.EventBasicArgs;
import com.eventty.eventtynextgen.events.service.EventDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventBasicService eventBasicService;
    private final EventDetailsService eventDetailsService;

    @Override
    public void createEvent(CreateEventArgs args) {

        EventBasicArgs eventBasicArgs = EventBasicArgs.builder()
            .hostId(args.hostId())
            .title(args.title())
            .imageUrls(args.imageUrls())
            .category(args.category())
            .eventStartAt(args.eventStartAt())
            .eventEndAt(args.eventEndAt())
            .participantLimitPolicy(args.participantLimitPolicy())
            .maxParticipants(args.maxParticipants())
            .location(args.location())
            .build();

        EventBasic eventBasic = this.eventBasicService.saveEventBasic(eventBasicArgs);

        EventDetails eventDetails = this.eventDetailsService.saveEventDetails(eventBasic.getId(), args.description(), args.applyStartAt(), args.applyEndAt());
    }
}
