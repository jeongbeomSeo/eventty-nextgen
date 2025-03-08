package com.eventty.eventtynextgen.event.controller;

import com.eventty.eventtynextgen.shared.annotation.APIV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@APIV1
public class EventBaseController {

    private final String BASE_PATH = "/event";

    @GetMapping(BASE_PATH)
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}