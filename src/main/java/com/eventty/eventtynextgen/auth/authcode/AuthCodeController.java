package com.eventty.eventtynextgen.auth.authcode;

import com.eventty.eventtynextgen.auth.authcode.annotation.AuthCodeApiV1;
import com.eventty.eventtynextgen.auth.authcode.request.AuthCodeSendCodeRequestCommand;
import com.eventty.eventtynextgen.auth.authcode.request.AuthCodeValidateCodeRequestCommand;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeExistsEmailResponseView;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeSendCodeResponseView;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeValidateCodeResponseView;
import com.eventty.eventtynextgen.user.annotation.EmailRegexp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@AuthCodeApiV1
@RequiredArgsConstructor
@Tag(name = "인증 코드 관리·처리 API", description = "인증 코드 관리·처리와 관련된 API")
public class AuthCodeController {

    private final AuthCodeService authCodeService;

    @GetMapping("/exists-email")
    @Operation(summary = "현재 사용중인 이메일 확인 API")
    public ResponseEntity<AuthCodeExistsEmailResponseView> existsEmail(@EmailRegexp @RequestParam("email") String email) {
        return ResponseEntity.ok(this.authCodeService.exists(email));
    }

    @PostMapping
    @Operation(summary = "인증 코드 발송 요청 API")
    public ResponseEntity<AuthCodeSendCodeResponseView> sendCode(
        @RequestBody @Valid AuthCodeSendCodeRequestCommand authCodeSendCodeRequestCommand) {
        return ResponseEntity.ok(this.authCodeService.sendCode(authCodeSendCodeRequestCommand.email()));
    }

    @PostMapping("/validate")
    @Operation(summary = "인증 코드 검증 요청 API")
    public ResponseEntity<AuthCodeValidateCodeResponseView> validateCode(
        @RequestBody @Valid AuthCodeValidateCodeRequestCommand authCodeValidateCodeRequestCommand) {
        return ResponseEntity.ok(
            this.authCodeService.validateCode(authCodeValidateCodeRequestCommand.email(), authCodeValidateCodeRequestCommand.code()));
    }
}
