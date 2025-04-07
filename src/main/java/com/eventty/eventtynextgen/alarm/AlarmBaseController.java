package com.eventty.eventtynextgen.alarm;

import com.eventty.eventtynextgen.alarm.shared.annotation.AlarmApiV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@AlarmApiV1
public class AlarmBaseController {

    private final String BASE_PATH = "/alarm";

    @GetMapping(BASE_PATH)
    public ResponseEntity<String> example() {
        return ResponseEntity.ok().build();
    }
}