package com.eventty.eventtynextgen.events;

import com.eventty.eventtynextgen.events.shared.annotation.EventApiV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@EventApiV1
public class EventsController {

    @GetMapping()
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}