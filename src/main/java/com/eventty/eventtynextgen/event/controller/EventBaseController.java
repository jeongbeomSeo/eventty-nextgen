package com.eventty.eventtynextgen.event.controller;

import com.eventty.eventtynextgen.event.shared.annotation.APIV1Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@APIV1Event
public class EventBaseController {

    @GetMapping("")
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}