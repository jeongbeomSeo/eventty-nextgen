package com.eventty.eventtynextgen.certification;


import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import com.eventty.eventtynextgen.certification.request.CertificationLoginRequestCommand;
import com.eventty.eventtynextgen.certification.response.CertificationLoginResponseView;
import com.eventty.eventtynextgen.certification.shared.annotation.CertificationApiV1;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CertificationApiV1
@RequiredArgsConstructor
@Tag(name = "인증 관리 API", description = "사용자 인증을 위한 ID 유효성 검증 및 인증 코드 처리와 관련된 API")
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    public ResponseEntity<CertificationLoginResponseView> login(@RequestBody @Valid CertificationLoginRequestCommand certificationLoginRequestCommand,
        HttpServletResponse response) {
        return ResponseEntity.ok(this.certificationService.login(
            certificationLoginRequestCommand.loginId(),
            certificationLoginRequestCommand.password(),
            response));
    }

    @LoginRequired
    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        Long userId = AuthorizationContextHolder.getContext().getUserId();

        certificationService.logout(userId, res);

        return ResponseEntity.ok().build();
    }
}
