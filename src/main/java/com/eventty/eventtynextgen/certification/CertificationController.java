package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.request.CertificationSendCodeRequestCommand;
import com.eventty.eventtynextgen.certification.request.CertificationValidateCodeRequestCommand;
import com.eventty.eventtynextgen.certification.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationValidateCodeResponseView;
import com.eventty.eventtynextgen.certification.shared.annotation.CertificationApiV1;
import com.eventty.eventtynextgen.user.shared.annotation.EmailRegexp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@CertificationApiV1
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/exists")
    public ResponseEntity<CertificationExistsResponseView> exists(@EmailRegexp @RequestParam("email") String email) {
        return ResponseEntity.ok(this.certificationService.checkExists(email));
    }

    @PostMapping
    public ResponseEntity<CertificationSendCodeResponseView> sendCode(
        @RequestBody @Valid CertificationSendCodeRequestCommand certificationSendCodeRequestCommand) {
        return ResponseEntity.ok(this.certificationService.sendCode(certificationSendCodeRequestCommand.email()));
    }

    @PostMapping("/validate")
    public ResponseEntity<CertificationValidateCodeResponseView> validateCode(
        @RequestBody @Valid CertificationValidateCodeRequestCommand certificationValidateCodeRequestCommand) {
        return ResponseEntity.ok(
            this.certificationService.validateCode(certificationValidateCodeRequestCommand.email(), certificationValidateCodeRequestCommand.code()));
    }

}
