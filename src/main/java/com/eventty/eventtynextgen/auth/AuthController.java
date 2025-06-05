package com.eventty.eventtynextgen.auth;


import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import com.eventty.eventtynextgen.auth.request.AuthLoginRequestCommand;
import com.eventty.eventtynextgen.auth.request.AuthReissueSessionTokenRequestCommand;
import com.eventty.eventtynextgen.auth.response.AuthLoginResponseView;
import com.eventty.eventtynextgen.auth.response.AuthReissueSessionTokenResponseView;
import com.eventty.eventtynextgen.auth.annotation.AuthApiV1;
import com.eventty.eventtynextgen.auth.shared.utils.CookieUtils;
import com.eventty.eventtynextgen.shared.context.AuthorizationContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AuthApiV1
@RequiredArgsConstructor
@Tag(name = "사용자 인증 관리 API", description = "사용자 인증을 위한 유효성 검증 및 인증 코드 처리와 관련된 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    public ResponseEntity<AuthLoginResponseView> login(
        @RequestBody @Valid AuthLoginRequestCommand authLoginRequestCommand,
        HttpServletResponse response) {
        return ResponseEntity.ok(this.authService.login(
            authLoginRequestCommand.loginId(),
            authLoginRequestCommand.password(),
            response));
    }

    @LoginRequired
    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Long userId = AuthorizationContextHolder.getContext().getUserId();

        authService.logout(userId, response);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue/session-token")
    @Operation(summary = "토큰 재발급 요청")
    public ResponseEntity<AuthReissueSessionTokenResponseView> reissueSessionToken(
        @RequestBody @Valid AuthReissueSessionTokenRequestCommand authReissueSessionTokenRequestCommand,
        HttpServletRequest request,
        HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookie(CookieUtils.REFRESH_TOKEN_HEADER_NAME, request);

        return ResponseEntity.ok(this.authService.reissueSessionToken(
            authReissueSessionTokenRequestCommand.userId(),
            authReissueSessionTokenRequestCommand.accessToken(),
            refreshToken,
            response
        ));
    }
}
