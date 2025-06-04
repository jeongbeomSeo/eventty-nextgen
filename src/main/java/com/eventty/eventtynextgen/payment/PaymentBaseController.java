package com.eventty.eventtynextgen.payment;

import com.eventty.eventtynextgen.payment.annotation.PaymentApiV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@PaymentApiV1
public class PaymentBaseController {

    private static final String BASE_PATH = "/payment";

    @GetMapping(BASE_PATH)
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}