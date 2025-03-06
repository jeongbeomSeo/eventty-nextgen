package com.eventty.eventtynextgen.ticket.controller;

import com.eventty.eventtynextgen.ticket.shared.annotation.APIV1Ticket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@APIV1Ticket
public class TicketBaseController {

    @GetMapping("")
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}