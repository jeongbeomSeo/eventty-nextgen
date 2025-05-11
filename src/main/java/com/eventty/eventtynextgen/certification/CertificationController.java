package com.eventty.eventtynextgen.certification;


import com.eventty.eventtynextgen.certification.request.CertificationLoginRequestCommand;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.shared.annotation.CertificationApiV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
@CertificationApiV1
@RequiredArgsConstructor
@Tag(name = "인증 코드 관리 API", description = "사용자 인증을 위한 ID 유효성 검증 및 인증 코드 처리와 관련된 API")
public class CertificationController {

    private CertificationService certificationService;


    @PostMapping("/login")
    public ResponseEntity<CertificationLoginResponseView> login(@RequestBody @Valid CertificationLoginRequestCommand certificationLoginRequestCommand) {
        return ResponseEntity.ok(
            this.certificationService.login(certificationLoginRequestCommand.loginId(), certificationLoginRequestCommand.password())
        );
    }
}
