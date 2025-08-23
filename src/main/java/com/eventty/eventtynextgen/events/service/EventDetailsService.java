package com.eventty.eventtynextgen.events.service;

import com.eventty.eventtynextgen.events.entity.EventDetails;
import com.eventty.eventtynextgen.events.repository.EventDetailsRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventDetailsService {

    private final EventDetailsRepository eventDetailsRepository;

    public EventDetails saveEventDetails(Long eventBasicId, String description, LocalDateTime applyStartAt, LocalDateTime applyEndAt) {

        EventDetails eventDetails = EventDetails.of(eventBasicId, description, applyStartAt, applyEndAt);

        return this.eventDetailsRepository.save(eventDetails);
    }
}
