package com.eventty.eventtynextgen.user;

import com.eventty.eventtynextgen.shared.annotation.APIV1;
import com.eventty.eventtynextgen.user.request.UserRequestCommand;
import com.eventty.eventtynextgen.user.request.UserUpdateRequestCommand;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@APIV1
@RequiredArgsConstructor
public class UserController {

    private final String BASE_PATH = "/user";

    private final UserService userService;

    @PostMapping(BASE_PATH)
    public ResponseEntity<UserSignupResponseView> signup(
        @RequestBody @Valid UserRequestCommand userRequestCommand) {

        UserSignupResponseView userSignupResponseView = this.userService.signup(
            userRequestCommand.email(), userRequestCommand.password(), userRequestCommand.userRole(),
            userRequestCommand.name(), userRequestCommand.phone(), userRequestCommand.birth());

//        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{userId}")
//            .buildAndExpand(userId).toUri();

        return ResponseEntity.ok(userSignupResponseView);
    }

    @PatchMapping(BASE_PATH)
    public ResponseEntity<UserUpdateResponseView> update(
        @RequestBody @Valid UserUpdateRequestCommand updateUserRequest) {

        UserUpdateResponseView userUpdateResponseView = this.userService.updateUser(
            updateUserRequest.id(), updateUserRequest.name(), updateUserRequest.phone(), updateUserRequest.birth());

        return ResponseEntity.ok(userUpdateResponseView);
    }

    @DeleteMapping(BASE_PATH)
    public ResponseEntity<UserDeleteResponseView> delete(
        @RequestParam(value = "user-id") Long userId) {

        UserDeleteResponseView userDeleteResponseView = this.userService.delete(userId);

        return ResponseEntity.ok(userDeleteResponseView);
    }
}