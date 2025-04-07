package com.eventty.eventtynextgen.payment;

import com.eventty.eventtynextgen.payment.shared.annotation.PaymentApiV1;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@PaymentApiV1
public class PaymentBaseController {

    private final String BASE_PATH = "/payment";

    @GetMapping(BASE_PATH)
    public ResponseEntity<Void> example() {
        return ResponseEntity.ok().build();
    }
}