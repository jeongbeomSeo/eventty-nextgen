package com.eventty.eventtynextgen.payment.controller;

import com.eventty.eventtynextgen.payment.shared.annotation.APIV1Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@APIV1Payment
public class PaymentBaseController {

    @GetMapping("")
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}