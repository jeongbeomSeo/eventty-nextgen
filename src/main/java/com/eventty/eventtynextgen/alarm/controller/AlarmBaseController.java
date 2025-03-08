package com.eventty.eventtynextgen.alarm.controller;

import com.eventty.eventtynextgen.shared.annotation.APIV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@APIV1
public class AlarmBaseController {

    private final String BASE_PATH = "/alarm";

    @GetMapping(BASE_PATH)
    public ResponseEntity<String> example() {
        return ResponseEntity.ok().build();
    }
}