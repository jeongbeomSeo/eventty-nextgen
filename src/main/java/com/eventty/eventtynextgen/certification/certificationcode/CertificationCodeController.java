package com.eventty.eventtynextgen.certification.certificationcode;

import com.eventty.eventtynextgen.certification.certificationcode.shared.annotation.CertificationCodeApiV1;
import com.eventty.eventtynextgen.certification.certificationcode.request.CertificationSendCodeRequestCommand;
import com.eventty.eventtynextgen.certification.certificationcode.request.CertificationValidateCodeRequestCommand;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationValidateCodeResponseView;
import com.eventty.eventtynextgen.user.shared.annotation.EmailRegexp;
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
@CertificationCodeApiV1
@RequiredArgsConstructor
@Tag(name = "인증 코드 관리 API", description = "사용자 인증을 위한 ID 유효성 검증 및 인증 코드 처리와 관련된 API")
public class CertificationCodeController {

    private final CertificationCodeService certificationService;

    @GetMapping("/exists")
    @Operation(summary = "현재 사용중인 이메일 확인 API")
    public ResponseEntity<CertificationExistsResponseView> exists(@EmailRegexp @RequestParam("email") String email) {
        return ResponseEntity.ok(this.certificationService.checkExists(email));
    }

    @PostMapping
    @Operation(summary = "인증 코드 발송 요청 API")
    public ResponseEntity<CertificationSendCodeResponseView> sendCode(
        @RequestBody @Valid CertificationSendCodeRequestCommand certificationSendCodeRequestCommand) {
        return ResponseEntity.ok(this.certificationService.sendCode(certificationSendCodeRequestCommand.email()));
    }

    @PostMapping("/validate")
    @Operation(summary = "인증 코드 검증 요청 API")
    public ResponseEntity<CertificationValidateCodeResponseView> validateCode(
        @RequestBody @Valid CertificationValidateCodeRequestCommand certificationValidateCodeRequestCommand) {
        return ResponseEntity.ok(
            this.certificationService.validateCode(certificationValidateCodeRequestCommand.email(), certificationValidateCodeRequestCommand.code()));
    }
}
