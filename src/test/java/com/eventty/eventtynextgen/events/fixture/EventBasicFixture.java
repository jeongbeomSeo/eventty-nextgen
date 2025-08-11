package com.eventty.eventtynextgen.events.fixture;

import com.eventty.eventtynextgen.events.entity.EventBasic;
import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import java.time.LocalDateTime;

public class EventBasicFixture {

    public static EventBasic createEventBasic() {
        return EventBasic.of(1L, "행사 title", "image url", EventCategoryType.ETC, LocalDateTime.now(), LocalDateTime.now().plusDays(3L),
            EventParticipantLimitPolicyType.LIMITED, 1000, "행사 장소");
    }

}
