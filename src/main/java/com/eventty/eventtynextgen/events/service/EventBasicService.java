package com.eventty.eventtynextgen.events.service;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.EventsErrorType;
import com.eventty.eventtynextgen.events.component.EventBasicValidator;
import com.eventty.eventtynextgen.events.component.EventBasicValidator.VerifyResult;
import com.eventty.eventtynextgen.events.entity.EventBasic;
import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import com.eventty.eventtynextgen.events.repository.EventBasicRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventBasicService {

    private final EventBasicRepository eventBasicRepository;
    private final EventBasicValidator eventBasicValidator;

    public EventBasic saveEventBasic(EventBasicArgs args) {

        VerifyResult verifyResult = this.eventBasicValidator.validateEventBasic(args.eventStartAt, args.eventEndAt, args.imageUrls(), args.participantLimitPolicy, args.maxParticipants);
        handleVerifyResult(verifyResult);

        EventBasic eventBasic = EventBasic.of(args.hostId, args.title, args.imageUrls, args.category, args.eventStartAt, args.eventEndAt, args.participantLimitPolicy, args.maxParticipants, args.location);

        return this.eventBasicRepository.save(eventBasic);
    }

    private void handleVerifyResult(VerifyResult verifyResult) {
        switch (verifyResult.getVerifyEventBasicResult()) {
            case ILLEGAL_EVENT_END_BEFORE_START -> throw CustomException.badRequest(EventsErrorType.ILLEGAL_EVENT_END_BEFORE_START, verifyResult.getDetails());
            case ILLEGAL_EVENT_IMAGE -> throw CustomException.badRequest(EventsErrorType.ILLEGAL_EVENT_IMAGE, verifyResult.getDetails());
            case ILLEGAL_ARGUMENT_MAX_PARTICIPANTS -> throw CustomException.badRequest(EventsErrorType.ILLEGAL_MAX_PARTICIPANTS, verifyResult.getDetails());
        }
    }

    @Builder
    public record EventBasicArgs(
        Long hostId,
        String title,
        List<String> imageUrls,
        EventCategoryType category,
        LocalDateTime eventStartAt,
        LocalDateTime eventEndAt,
        EventParticipantLimitPolicyType participantLimitPolicy,
        Integer maxParticipants,
        String location
    ) {}
}
