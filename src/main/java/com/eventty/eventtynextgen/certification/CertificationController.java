package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.request.CertificationIssueCertificationTokenRequestCommand;
import com.eventty.eventtynextgen.certification.response.CertificationIssueCertificationTokenResponseView;
import com.eventty.eventtynextgen.certification.annotation.CertificationApiV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CertificationApiV1
@RequiredArgsConstructor
@Tag(name = "서비스 인증 관리 API", description = "플랫폼을 이용하는 서비스 인증을 위한 토큰 발급을 위한 API")
public class CertificationController {

    private final CertificationService certificationService;

    // TODO: Request Body를 받는 것이 아닌, Request Param required true 를 통해서 받도록 API 스펙 수정
    @PostMapping("/issue/certification-token")
    public ResponseEntity<CertificationIssueCertificationTokenResponseView> issueCertificationToken(@RequestBody
    CertificationIssueCertificationTokenRequestCommand certificationIssueCertificationTokenRequestCommand) {
        return ResponseEntity.ok(this.certificationService.issueCertificationToken(certificationIssueCertificationTokenRequestCommand.accessToken()));
    }
}
