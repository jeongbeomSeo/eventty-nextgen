package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.annotation.CertificationApiV1;
import com.eventty.eventtynextgen.certification.request.CertificationIssueTokenRequestCommand;
import com.eventty.eventtynextgen.certification.response.CertificationIssueTokenResponseView;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CertificationApiV1
@RequiredArgsConstructor
@Tag(name = "서비스 인증 관리 API", description = "플랫폼을 이용하는 서비스 인증을 위한 토큰 발급을 위한 API")
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping("/issue/certification-token")
    public ResponseEntity<CertificationIssueTokenResponseView> issueCertificationToken(
        @RequestBody @Valid CertificationIssueTokenRequestCommand certificationIssueTokenRequestCommand,
        HttpServletResponse response) {
        CertificationIssueTokenResponseView certificationIssueTokenResponseView = this.certificationService.issueCertificationToken(
            certificationIssueTokenRequestCommand.appName(),
            certificationIssueTokenRequestCommand.appSecret(),
            response);
        return ResponseEntity.ok(certificationIssueTokenResponseView);
    }
}
