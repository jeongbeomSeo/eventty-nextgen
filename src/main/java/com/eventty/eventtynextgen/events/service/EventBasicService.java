package com.eventty.eventtynextgen.events.service;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.EventsErrorType;
import com.eventty.eventtynextgen.events.component.CreateEventValidator;
import com.eventty.eventtynextgen.events.component.CreateEventValidator.VerifyResult;
import com.eventty.eventtynextgen.events.entity.EventBasic;
import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import com.eventty.eventtynextgen.events.repository.EventBasicRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventBasicService {

    private final EventBasicRepository eventBasicRepository;
    private final CreateEventValidator createEventValidator;

    public EventBasic saveEventBasic(Long hostId, String title, List<String> imageUrls, EventCategoryType category, LocalDateTime eventStartAt, LocalDateTime eventEndAt, EventParticipantLimitPolicyType participantLimitPolicy, Integer maxParticipants, String location) {

        VerifyResult verifyResult = this.createEventValidator.validateEventBasic(eventStartAt, eventEndAt, imageUrls);
        handleVerifyResult(verifyResult);

        EventBasic eventBasic = EventBasic.of(hostId, title, imageUrls, category, eventStartAt, eventEndAt, participantLimitPolicy, maxParticipants, location);

        return this.eventBasicRepository.save(eventBasic);
    }

    private void handleVerifyResult(VerifyResult verifyResult) {
        switch (verifyResult.getVerifyEventBasicResult()) {
            case ILLEGAL_EVENT_END_BEFORE_START -> throw CustomException.badRequest(EventsErrorType.ILLEGAL_EVENT_END_BEFORE_START, verifyResult.getDetails());
            case ILLEGAL_EVENT_IMAGE -> throw CustomException.badRequest(EventsErrorType.ILLEGAL_EVENT_IMAGE, verifyResult.getDetails());
        }
    }

}
