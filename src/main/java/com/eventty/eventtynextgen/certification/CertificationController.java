package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.request.CertificationExistsRequestCommand;
import com.eventty.eventtynextgen.certification.request.CertificationRequestCommand;
import com.eventty.eventtynextgen.certification.request.CertificationValidateRequestCommand;
import com.eventty.eventtynextgen.certification.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationValidateCodeResponseView;
import com.eventty.eventtynextgen.shared.annotation.APIV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@APIV1
@RequiredArgsConstructor
public class CertificationController {

    private final String BASE_PATH = "/user/certification";

    private final CertificationService certificationService;

    @PostMapping(BASE_PATH + "/exists")
    public ResponseEntity<CertificationExistsResponseView> exists(
        @RequestBody @Valid CertificationExistsRequestCommand certificationExistsRequestCommand) {
        return ResponseEntity.ok(
            this.certificationService.checkExists(certificationExistsRequestCommand.email())
        );
    }

    @PostMapping(BASE_PATH)
    public ResponseEntity<CertificationSendCodeResponseView> sendCode(
        @RequestBody @Valid CertificationRequestCommand certificationRequestCommand) {
        return ResponseEntity.ok(
            this.certificationService.sendCode(certificationRequestCommand.email())
        );
    }

    @PostMapping(BASE_PATH + "/validate")
    public ResponseEntity<CertificationValidateCodeResponseView> validateCode(
        @RequestBody @Valid CertificationValidateRequestCommand certificationValidateRequestCommand) {
        return ResponseEntity.ok(
            this.certificationService.validateCode(certificationValidateRequestCommand.code())
        );
    }

}
