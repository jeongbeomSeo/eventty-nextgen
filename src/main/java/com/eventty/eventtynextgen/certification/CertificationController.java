package com.eventty.eventtynextgen.certification;

import com.eventty.eventtynextgen.certification.annotation.CertificationApiV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CertificationApiV1
@RequiredArgsConstructor
@Tag(name = "서비스 인증 관리 API", description = "플랫폼을 이용하는 서비스 인증을 위한 토큰 발급을 위한 API")
public class CertificationController {

    private final CertificationService certificationService;

    @GetMapping("/issue/certification-token")
    public ResponseEntity<Void> issueCertificationToken(
        @RequestParam(name = "appName") String appName,
        @RequestParam(name = "appSecret") String appSecret,
        HttpServletResponse response) {
        this.certificationService.issueCertificationToken(appName, appSecret, response);
        return ResponseEntity.ok().build();
    }
}
