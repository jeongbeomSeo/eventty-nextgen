package com.eventty.eventtynextgen.auth.controller;

import com.eventty.eventtynextgen.shared.annotation.APIV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@APIV1
public class AuthController {

    private final String BASE_PATH = "/auth";

    @PostMapping(BASE_PATH)
    public ResponseEntity<Void> signup() {
        return ResponseEntity.ok().build();
    }
}