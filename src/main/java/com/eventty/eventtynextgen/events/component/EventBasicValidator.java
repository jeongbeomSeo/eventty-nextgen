package com.eventty.eventtynextgen.events.component;

import com.eventty.eventtynextgen.component.StorageService;
import com.eventty.eventtynextgen.component.StorageService.Purpose;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventBasicValidator {

    private final StorageService storageService;

    public VerifyResult validateEventBasic(LocalDateTime eventStartAt, LocalDateTime eventEndAt, List<String> imageUrls, EventParticipantLimitPolicyType participantLimitPolicy, Integer maxParticipants) {
        if (!validateEventTime(eventStartAt, eventEndAt)) {
            return new VerifyResult(VerifyEventBasicResult.ILLEGAL_EVENT_END_BEFORE_START, "eventEndAt: " + eventEndAt + " is before eventStartAt: " + eventStartAt);
        }

        if (!validateStorageImage(imageUrls)) {
            return new VerifyResult(VerifyEventBasicResult.ILLEGAL_EVENT_IMAGE, "imageUrls: " + imageUrls.stream().filter((url) -> !isStorageUrl(url)).toList());
        }

        if (!validateParticipantLimitPolicy(participantLimitPolicy, maxParticipants)) {
            return new VerifyResult(VerifyEventBasicResult.ILLEGAL_ARGUMENT_MAX_PARTICIPANTS, "maxParticipants can not be negative number or zero: " + maxParticipants);
        }

        return new VerifyResult(VerifyEventBasicResult.VERIFIED, "");
    }

    private boolean validateEventTime(LocalDateTime eventStartAt, LocalDateTime eventEndAt) {
        return eventStartAt.isBefore(eventEndAt);
    }

    private boolean validateStorageImage(List<String> imageUrls) {
        return imageUrls == null || imageUrls.stream().allMatch(this::isStorageUrl);
    }

    private boolean validateParticipantLimitPolicy(EventParticipantLimitPolicyType participantLimitPolicy, Integer maxParticipants) {
        return participantLimitPolicy == EventParticipantLimitPolicyType.UNLIMITED || (maxParticipants != null && maxParticipants > 0);
    }

    private boolean isStorageUrl(String fileUrl) {
        return this.storageService.existsFile(fileUrl, Purpose.EVENT_IMAGE);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VerifyResult {
        private VerifyEventBasicResult verifyEventBasicResult;
        private String details;
    }

    public enum VerifyEventBasicResult {
        VERIFIED,
        ILLEGAL_EVENT_END_BEFORE_START,
        ILLEGAL_EVENT_IMAGE,
        ILLEGAL_ARGUMENT_MAX_PARTICIPANTS
    }
}
