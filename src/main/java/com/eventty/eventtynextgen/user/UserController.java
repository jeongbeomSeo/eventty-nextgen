package com.eventty.eventtynextgen.user;

import com.eventty.eventtynextgen.user.request.UserChangePasswordRequestCommand;
import com.eventty.eventtynextgen.user.request.UserSignUpRequestCommand;
import com.eventty.eventtynextgen.user.request.UserUpdateRequestCommand;
import com.eventty.eventtynextgen.user.response.UserActivateDeletedUserResponseView;
import com.eventty.eventtynextgen.user.response.UserChangePasswordResponseView;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserFindEmailResponseView;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;
import com.eventty.eventtynextgen.user.annotation.UserApiV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@UserApiV1
@Validated
@RequiredArgsConstructor
@Tag(name = "사용자 관리 API", description = "사용자 관리 및 조작을 위한 API")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "회원가입 API")
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
    @Operation(summary = "사용자 정보 수정 API")
    public ResponseEntity<UserUpdateResponseView> update(@RequestBody @Valid UserUpdateRequestCommand updateUserRequest) {
        UserUpdateResponseView userUpdateResponseView = this.userService.update(
            updateUserRequest.id(),
            updateUserRequest.name(),
            updateUserRequest.phone(),
            updateUserRequest.birth());

        return ResponseEntity.ok(userUpdateResponseView);
    }

    @DeleteMapping
    @Operation(summary = "사용자 삭제 API")
    public ResponseEntity<UserDeleteResponseView> delete(@RequestParam(value = "user-id") Long userId) {
        return ResponseEntity.ok(this.userService.delete(userId));
    }

    @PatchMapping("/{user-id}/status")
    @Operation(summary = "삭제된 사용자의 상태를 활성화 상태로 변경 API")
    public ResponseEntity<UserActivateDeletedUserResponseView> activateDeletedUser(
        @PathVariable("user-id") Long userId) {
        return ResponseEntity.ok(this.userService.activateToDeletedUser(userId));
    }

    @GetMapping(value = "/email", params = {"name", "phone"})
    @Operation(summary = "사용자 이름과 전화번호를 통해서 이메일 찾기 API")
    public ResponseEntity<UserFindEmailResponseView> findEmail(
        @NotBlank(message = "이름은 null이거나 빈 문자열일 수 없습니다.")
        @RequestParam("name") String name,
        @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "핸드폰 번호는 000-0000-0000의 형식이어야 합니다.")
        @RequestParam("phone") String phone) {
        return ResponseEntity.ok(this.userService.findEmailByPersonalInfo(name, phone));
    }

    @PatchMapping("/password")
    @Operation(summary = "사용자 패스워드 변경 API")
    public ResponseEntity<UserChangePasswordResponseView> changePassword(
        @RequestBody @Valid UserChangePasswordRequestCommand userChangePasswordRequestCommand) {
        return ResponseEntity.ok(this.userService.changePassword(
            userChangePasswordRequestCommand.userId(),
            userChangePasswordRequestCommand.currentPassword(),
            userChangePasswordRequestCommand.updatedPassword()));
    }
}