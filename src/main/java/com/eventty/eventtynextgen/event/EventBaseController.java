package com.eventty.eventtynextgen.event;

import com.eventty.eventtynextgen.event.shared.annotation.EventApiV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@EventApiV1
public class EventBaseController {

    private final String BASE_PATH = "/event";

    @GetMapping(BASE_PATH)
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}