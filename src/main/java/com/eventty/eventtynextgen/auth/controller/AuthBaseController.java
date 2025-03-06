package com.eventty.eventtynextgen.auth.controller;

import com.eventty.eventtynextgen.auth.shared.annotation.APIv1Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@APIv1Auth
public class AuthBaseController {

    @GetMapping("")
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}