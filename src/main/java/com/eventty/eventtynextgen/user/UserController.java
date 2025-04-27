package com.eventty.eventtynextgen.user;

import com.eventty.eventtynextgen.user.request.UserFindAccountRequestCommand;
import com.eventty.eventtynextgen.user.request.UserSignUpRequestCommand;
import com.eventty.eventtynextgen.user.request.UserUpdateRequestCommand;
import com.eventty.eventtynextgen.user.response.UserActivateDeletedUserResponseView;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserFindAccountResponseView;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;
import com.eventty.eventtynextgen.user.shared.annotation.UserApiV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@UserApiV1
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserSignupResponseView> signup(@RequestBody @Valid UserSignUpRequestCommand userSignUpRequestCommand) {
        UserSignupResponseView userSignupResponseView = this.userService.signup(
            userSignUpRequestCommand.email(),
            userSignUpRequestCommand.password(),
            userSignUpRequestCommand.userRole(),
            userSignUpRequestCommand.name(),
            userSignUpRequestCommand.phone(),
            userSignUpRequestCommand.birth());

        return ResponseEntity.status(HttpStatus.CREATED).body(userSignupResponseView);
    }

    @PatchMapping
    public ResponseEntity<UserUpdateResponseView> update(@RequestBody @Valid UserUpdateRequestCommand updateUserRequest) {
        UserUpdateResponseView userUpdateResponseView = this.userService.update(
            updateUserRequest.id(),
            updateUserRequest.name(),
            updateUserRequest.phone(),
            updateUserRequest.birth());

        return ResponseEntity.ok(userUpdateResponseView);
    }

    @DeleteMapping
    public ResponseEntity<UserDeleteResponseView> delete(@RequestParam(value = "user-id") Long userId) {
        return ResponseEntity.ok(this.userService.delete(userId));
    }

    @PatchMapping("/{user-id}/status")
    public ResponseEntity<UserActivateDeletedUserResponseView> activateDeletedUser(
        @PathVariable("user-id") Long userId) {
        return ResponseEntity.ok(this.userService.activateDeletedUser(userId));
    }

    @GetMapping("/accounts")
    public ResponseEntity<UserFindAccountResponseView> findAccount(@RequestBody @Valid UserFindAccountRequestCommand userFindAccountRequestCommand) {
        return ResponseEntity.ok(this.userService.findAccount(userFindAccountRequestCommand.name(), userFindAccountRequestCommand.phone()));
    }
}