package com.eventty.eventtynextgen.user.controller;

import com.eventty.eventtynextgen.shared.annotation.APIV1;
import com.eventty.eventtynextgen.user.model.request.SignupRequest;
import com.eventty.eventtynextgen.user.model.request.UpdateUserRequest;
import com.eventty.eventtynextgen.user.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@APIV1
@RequiredArgsConstructor
public class UserController {

    private final String BASE_PATH = "/user";

    private final UserService userService;

    @PostMapping(BASE_PATH)
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest signupRequest) {

        Long userId = userService.signup(signupRequest);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{userId}")
            .buildAndExpand(userId)
            .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping(BASE_PATH)
    public ResponseEntity<Void> updateUser(
        @RequestBody @Valid UpdateUserRequest updateUserRequest) {

        userService.updateUser(updateUserRequest);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(BASE_PATH)
    public ResponseEntity<Void> deleteUser(@RequestParam(value = "user-id") Long userId) {

        userService.delete(userId);

        return ResponseEntity.ok().build();
    }
}