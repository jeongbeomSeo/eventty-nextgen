package com.eventty.alarm.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alarm")
public class BaseController {

    @GetMapping("/")
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}
