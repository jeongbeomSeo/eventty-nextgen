package com.eventty.eventtynextgen.events.fixture;

import com.eventty.eventtynextgen.events.entity.EventDetails;
import java.time.LocalDateTime;

public class EventDetailsFixture {

    public static EventDetails createEventDetails() {
        return EventDetails.of(1L, "행사 세부 정보 상세 설명", LocalDateTime.now(), LocalDateTime.now().plusDays(3L));
    }
}
