package com.eventty.eventtynextgen.events.fixture;

import com.eventty.eventtynextgen.events.entity.EventInterest;

public class EventInterestFixture {

    public static EventInterest createEventInterest() {
        return EventInterest.of(1L, 1L);
    }

}
