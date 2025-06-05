package com.eventty.eventtynextgen.auth.authcode;

import static com.eventty.eventtynextgen.auth.constant.AuthConst.AUTH_CODE_TTL;
import static com.eventty.eventtynextgen.auth.constant.AuthConst.EMAIL_VERIFICATION_CODE_LEN;

import com.eventty.eventtynextgen.auth.authcode.entity.AuthCode;
import com.eventty.eventtynextgen.auth.authcode.repository.AuthCodeRepository;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeExistsEmailResponseView;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeSendCodeResponseView;
import com.eventty.eventtynextgen.auth.authcode.response.AuthCodeValidateCodeResponseView;
import com.eventty.eventtynextgen.component.EmailSenderService;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.AuthErrorType;
import com.eventty.eventtynextgen.shared.utils.CodeGeneratorUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthCodeServiceImpl implements AuthCodeService {

    private final UserComponent userComponent;
    private final AuthCodeRepository authCodeRepository;
    private final EmailSenderService emailSenderService;

    @Override
    public AuthCodeExistsEmailResponseView exists(String authTarget) {
        return new AuthCodeExistsEmailResponseView(authTarget, this.userComponent.getActivatedUserByEmail(authTarget).isPresent());
    }

    @Override
    public AuthCodeSendCodeResponseView sendCode(String authTarget) {
        String code = CodeGeneratorUtil.generateVerificationCode(EMAIL_VERIFICATION_CODE_LEN);

        AuthCode authCode = AuthCode.of(authTarget, code, AUTH_CODE_TTL);
        AuthCode authCodeFromDb = this.authCodeRepository.save(authCode);
        if (authCodeFromDb.getId() != null) {
            this.emailSenderService.sendEmailVerificationMail(authTarget, code);
        } else {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, AuthErrorType.AUTH_CODE_SAVE_ERROR);
        }

        return AuthCodeSendCodeResponseView.createMessage(authTarget, AUTH_CODE_TTL);
    }

    @Override
    public AuthCodeValidateCodeResponseView validateCode(String email, String code) {
        boolean validate = true;
        try {
            AuthCode authCode = this.authCodeRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> CustomException.badRequest(AuthErrorType.MISMATCH_EMAIL_AUTH_CODE));

            if (authCode.isExpired() || checkExpired(authCode)) {
                authCode.setExpired();
                throw CustomException.badRequest(AuthErrorType.EXPIRE_EMAIL_AUTH_CODE);
            }
        } catch (Exception ex) {
            if (ex instanceof CustomException) {
                log.error("인증 코드 검증 중 CustomException 발생 code: {}, errorType: {}", code, ((CustomException) ex).getErrorType());
            } else {
                log.error("인증 코드 검증 중 일반 예외 발생 code: {}", code, ex);
            }
            validate = false;
        }

        return new AuthCodeValidateCodeResponseView(code, validate);
    }

    private boolean checkExpired(AuthCode authCode) {
        return authCode.getExpiredAt().isBefore(LocalDateTime.now());
    }
}
