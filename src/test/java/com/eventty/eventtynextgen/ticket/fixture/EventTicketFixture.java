package com.eventty.eventtynextgen.ticket.fixture;

import com.eventty.eventtynextgen.ticket.entity.EventTicket;
import com.eventty.eventtynextgen.ticket.entity.enums.EventTicketPurchaseLimitPolicyType;
import com.eventty.eventtynextgen.ticket.entity.enums.EventTicketQuantityLimitType;
import com.eventty.eventtynextgen.ticket.entity.enums.EventTicketStatusType;
import java.time.LocalDateTime;

public class EventTicketFixture {

    public static EventTicket createEventTicket() {
        return EventTicket.of(1L, "티켓 이름", "티켓 설명", 10000L, EventTicketQuantityLimitType.LIMITED, 1000, EventTicketPurchaseLimitPolicyType.LIMITED, 4,
            LocalDateTime.now(), LocalDateTime.now().plusDays(3L), EventTicketStatusType.ACTIVE);
    }

}
