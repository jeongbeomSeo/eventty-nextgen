package com.eventty.eventtynextgen.ticket;

import com.eventty.eventtynextgen.ticket.shared.annotation.TicketApiV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@TicketApiV1
public class TicketBaseController {

    @GetMapping
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}