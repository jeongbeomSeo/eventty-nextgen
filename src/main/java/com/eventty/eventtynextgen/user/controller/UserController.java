package com.eventty.eventtynextgen.user.controller;

import com.eventty.eventtynextgen.shared.annotation.APIV1;
import com.eventty.eventtynextgen.user.model.request.UpdateUserRequest;
import com.eventty.eventtynextgen.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@APIV1
@RequiredArgsConstructor
public class UserController {

    private final String BASE_PATH = "/user";

    private final UserService userService;

    @PatchMapping(BASE_PATH)
    public ResponseEntity<Void> updateUser(
        @RequestBody @Valid UpdateUserRequest updateUserRequest) {

        userService.updateUser(updateUserRequest);

        return ResponseEntity.noContent().build();
    }
}