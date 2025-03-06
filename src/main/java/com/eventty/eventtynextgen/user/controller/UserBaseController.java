package com.eventty.eventtynextgen.user.controller;

import com.eventty.eventtynextgen.user.shared.annotation.APIV1User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@APIV1User
public class UserBaseController {

    @GetMapping("")
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}