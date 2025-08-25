package com.eventty.eventtynextgen.events.service;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.EventsErrorType;
import com.eventty.eventtynextgen.events.component.EventDetailsValidator;
import com.eventty.eventtynextgen.events.component.EventDetailsValidator.VerifyResult;
import com.eventty.eventtynextgen.events.entity.EventDetails;
import com.eventty.eventtynextgen.events.repository.EventDetailsRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventDetailsService {

    private final EventDetailsValidator eventDetailsValidator;
    private final EventDetailsRepository eventDetailsRepository;

    public EventDetails saveEventDetails(Long eventBasicId, String description, LocalDateTime applyStartAt, LocalDateTime applyEndAt) {

        VerifyResult verifyResult = this.eventDetailsValidator.validateEventDetails(applyStartAt, applyEndAt);
        handleVerifyResult(verifyResult);

        EventDetails eventDetails = EventDetails.of(eventBasicId, description, applyStartAt, applyEndAt);

        return this.eventDetailsRepository.save(eventDetails);
    }

    private void handleVerifyResult(VerifyResult verifyResult) {
        switch (verifyResult.getVerifyEventDetailsResult()) {
            case ILLEGAL_ARGUMENT_APPLY_END_BEFORE_START -> throw CustomException.badRequest(EventsErrorType.ILLEGAL_APPLY_END_BEFORE_START, verifyResult.getDetails());
        }
    }
}
