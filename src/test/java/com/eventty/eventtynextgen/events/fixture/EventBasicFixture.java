package com.eventty.eventtynextgen.events.fixture;

import com.eventty.eventtynextgen.events.entity.EventBasic;
import com.eventty.eventtynextgen.events.entity.enums.EventCategoryType;
import com.eventty.eventtynextgen.events.entity.enums.EventParticipantLimitPolicyType;
import java.time.LocalDateTime;
import java.util.List;

public class EventBasicFixture {

    public static EventBasic createEventBasic() {
        return EventBasic.of(1L, "행사 title", List.of("https://example.com/image1.png", "https://example.com/image2.png"), EventCategoryType.ETC, LocalDateTime.now(), LocalDateTime.now().plusDays(3L),
            EventParticipantLimitPolicyType.LIMITED, 1000, "행사 장소");
    }

}
