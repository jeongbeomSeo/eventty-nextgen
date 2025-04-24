package com.eventty.eventtynextgen.user;

import com.eventty.eventtynextgen.user.request.UserSignUpRequestCommand;
import com.eventty.eventtynextgen.user.request.UserUpdateRequestCommand;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;
import com.eventty.eventtynextgen.user.shared.annotation.UserApiV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    // TODO: 삭제 되어 있는 유저 활성화 해주는 API 구현
}