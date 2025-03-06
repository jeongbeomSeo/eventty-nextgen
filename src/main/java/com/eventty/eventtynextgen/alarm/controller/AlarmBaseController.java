package com.eventty.eventtynextgen.alarm.controller;

import com.eventty.eventtynextgen.alarm.shared.annotation.APIV1Alarm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@APIV1Alarm
public class AlarmBaseController {

    @GetMapping("")
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}