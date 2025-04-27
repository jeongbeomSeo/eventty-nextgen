package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.request.CertificationExistsRequestCommand;
import com.eventty.eventtynextgen.certification.request.CertificationSendCodeRequestCommand;
import com.eventty.eventtynextgen.certification.request.CertificationValidateCodeRequestCommand;
import com.eventty.eventtynextgen.certification.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationValidateCodeResponseView;
import com.eventty.eventtynextgen.certification.shared.annotation.CertificationApiV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CertificationApiV1
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/exists")
    public ResponseEntity<CertificationExistsResponseView> exists(@RequestBody @Valid CertificationExistsRequestCommand certificationExistsRequestCommand) {
        return ResponseEntity.ok(this.certificationService.checkExists(certificationExistsRequestCommand.email()));
    }

    @PostMapping
    public ResponseEntity<CertificationSendCodeResponseView> sendCode(@RequestBody @Valid CertificationSendCodeRequestCommand certificationSendCodeRequestCommand) {
        return ResponseEntity.ok(this.certificationService.sendCode(certificationSendCodeRequestCommand.email()));
    }

    @PostMapping("/validate")
    public ResponseEntity<CertificationValidateCodeResponseView> validateCode(
        @RequestBody @Valid CertificationValidateCodeRequestCommand certificationValidateCodeRequestCommand) {
        return ResponseEntity.ok(
            this.certificationService.validateCode(certificationValidateCodeRequestCommand.email(), certificationValidateCodeRequestCommand.code()));
    }

}
