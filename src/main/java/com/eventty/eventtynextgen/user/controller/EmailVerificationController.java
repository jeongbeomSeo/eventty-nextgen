package com.eventty.eventtynextgen.user.controller;

import com.eventty.eventtynextgen.shared.annotation.APIV1;
import com.eventty.eventtynextgen.user.model.request.EmailVerificationRequest;
import com.eventty.eventtynextgen.user.model.request.EmailVerificationValidationRequest;
import com.eventty.eventtynextgen.user.model.response.EmailVerificationResponse;
import com.eventty.eventtynextgen.user.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@APIV1
@RequiredArgsConstructor
public class EmailVerificationController {

    private final String BASE_PATH = "/user/email";

    private final VerificationService verificationService;


    @GetMapping(BASE_PATH + "/exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam(value = "email") String email) {

        boolean result = verificationService.existsEmail(email);

        return ResponseEntity.ok(result);
    }

    @PostMapping(BASE_PATH + "/verification")
    public ResponseEntity<EmailVerificationResponse> emailVerification(@Valid @RequestBody
    EmailVerificationRequest request) {

        EmailVerificationResponse emailVerificationResponse = verificationService.sendEmailVerificationCode(
            request);

        return ResponseEntity.ok(emailVerificationResponse);
    }

    @PostMapping(BASE_PATH + "/verification-validation")
    public ResponseEntity<Void> emailVerificationValidation(@Valid @RequestBody
    EmailVerificationValidationRequest request) {

        boolean result = verificationService.checkValidationEmail(request);

        return ResponseEntity.ok().build();
    }
}
