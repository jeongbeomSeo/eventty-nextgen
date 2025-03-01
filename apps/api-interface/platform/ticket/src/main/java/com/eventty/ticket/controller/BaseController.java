package com.eventty.ticket.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ticket")
public class BaseController {

    @GetMapping("/")
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}
