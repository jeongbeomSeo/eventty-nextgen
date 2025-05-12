package com.eventty.eventtynextgen.certification;


import com.eventty.eventtynextgen.certification.CertificationService.CertificationLoginResult;
import com.eventty.eventtynextgen.certification.request.CertificationLoginRequestCommand;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.shared.annotation.CertificationApiV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CertificationApiV1
@RequiredArgsConstructor
@Tag(name = "인증 코드 관리 API", description = "사용자 인증을 위한 ID 유효성 검증 및 인증 코드 처리와 관련된 API")
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping("/login")
    public ResponseEntity<CertificationLoginResponseView> login(@RequestBody @Valid CertificationLoginRequestCommand certificationLoginRequestCommand) {
        CertificationLoginResult result = this.certificationService.login(certificationLoginRequestCommand.loginId(),
            certificationLoginRequestCommand.password());

        // RefreshToken으로 ResponseCookie 생성

        CertificationLoginResponseView certificationLoginResponseView = result.toCertificationLoginResponseView();

        // Header에 http only setCookie인 refreshToken 담기
        return ResponseEntity.ok(certificationLoginResponseView);
    }
}
