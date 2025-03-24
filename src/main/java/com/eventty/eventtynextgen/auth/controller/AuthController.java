package com.eventty.eventtynextgen.auth.controller;

import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.service.AuthService;
import com.eventty.eventtynextgen.shared.annotation.APIV1;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@APIV1
@RequiredArgsConstructor
public class AuthController {

    private final String BASE_PATH = "/auth";

    private final AuthService authService;

    @PostMapping(BASE_PATH)
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest signupRequest) {

        Long userId = authService.signup(signupRequest);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{userId}")
            .buildAndExpand(userId)
            .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(BASE_PATH + "/email-check")
    public ResponseEntity<Boolean> checkEmail(@RequestParam(value = "email") String email) {

        boolean result = authService.checkEmail(email);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping(BASE_PATH)
    public ResponseEntity<Void> deleteUser(@RequestParam(value = "auth-id") Long authUserId) {

        authService.delete(authUserId);

        return ResponseEntity.ok().build();
    }

    // TODO: 이메일 검증
}