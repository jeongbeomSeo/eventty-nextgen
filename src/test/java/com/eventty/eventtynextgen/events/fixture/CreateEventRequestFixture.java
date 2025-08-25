package com.eventty.eventtynextgen.events.fixture;

import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import com.eventty.eventtynextgen.events.request.EventsCreateEventRequestCommand;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class CreateEventRequestFixture {

    public static EventsCreateEventRequestCommand createEmptyImageRequest() {
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            Collections.emptyList(),
            EventCategoryType.ART,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(6),
            EventParticipantLimitPolicyType.UNLIMITED,
            null,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createNullImageRequest() {
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            null,
            EventCategoryType.ART,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(6),
            EventParticipantLimitPolicyType.UNLIMITED,
            null,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createParticipantPolicyTypeIsUnlimitedAndZeroMaxParticipants() {
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            null,
            EventCategoryType.ART,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(6),
            EventParticipantLimitPolicyType.UNLIMITED,
            0,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createStartEndTimeIsLateOnlyOneSecond() {
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            null,
            EventCategoryType.ART,
            LocalDateTime.now(),
            LocalDateTime.now().plusSeconds(1),
            EventParticipantLimitPolicyType.UNLIMITED,
            0,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createRequestWithGcsImageUrls(List<String> imageUrls) {
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            imageUrls,
            EventCategoryType.ART,
            LocalDateTime.now(),
            LocalDateTime.now().plusSeconds(1),
            EventParticipantLimitPolicyType.UNLIMITED,
            0,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createStartTimeAfterEndTimeRequest() {
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            null,
            EventCategoryType.ART,
            LocalDateTime.now(),
            LocalDateTime.now().minusDays(1),
            EventParticipantLimitPolicyType.UNLIMITED,
            0,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createStartTimeEqualsEndTimeRequest() {
        LocalDateTime now = LocalDateTime.now();
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            null,
            EventCategoryType.ART,
            now,
            now,
            EventParticipantLimitPolicyType.UNLIMITED,
            0,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createRequestWithWrongFormatImageUrl(List<String> validImageUrls) {
        List<String> imageUrls1 = new java.util.ArrayList<>(validImageUrls);
        imageUrls1.add("fs://image.url");
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            imageUrls1,
            EventCategoryType.ART,
            LocalDateTime.now(),
            LocalDateTime.now().minusDays(1),
            EventParticipantLimitPolicyType.UNLIMITED,
            0,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createRequestWithNotSavedImage(List<String> savedImageUrls) {
        List<String> imageUrls1 = new java.util.ArrayList<>(savedImageUrls);
        imageUrls1.add("https://image.url");
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            imageUrls1,
            EventCategoryType.ART,
            LocalDateTime.now(),
            LocalDateTime.now().minusDays(1),
            EventParticipantLimitPolicyType.UNLIMITED,
            0,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createParticipantLimitPolicyTypeIsLimitedAndMaxParticipantsIsNull() {
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            Collections.emptyList(),
            EventCategoryType.ART,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(6),
            EventParticipantLimitPolicyType.LIMITED,
            null,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createParticipantLimitPolicyTypeIsLimitedAndMaxParticipantsIsZero() {
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            Collections.emptyList(),
            EventCategoryType.ART,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(6),
            EventParticipantLimitPolicyType.LIMITED,
            0,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    public static EventsCreateEventRequestCommand createApplyStartTimeAfterApplyEndTime() {
        return new EventsCreateEventRequestCommand(
            "Spring Boot Workshop",
            Collections.emptyList(),
            EventCategoryType.ART,
            LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(6),
            EventParticipantLimitPolicyType.LIMITED,
            100,
            "서울특별시 강남구 테헤란로 123",
            "Join us for an in-depth Spring Boot workshop!",
            LocalDateTime.now().plusDays(4),
            LocalDateTime.now().plusDays(2)
        );
    }
}
