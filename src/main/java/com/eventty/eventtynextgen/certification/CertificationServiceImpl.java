package com.eventty.eventtynextgen.certification;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_CODE_TTL;
import static com.eventty.eventtynextgen.certification.constant.CertificationConst.EMAIL_VERIFICATION_CODE_LEN;

import com.eventty.eventtynextgen.certification.entity.CertificationCode;
import com.eventty.eventtynextgen.certification.repository.CertificationCodeRepository;
import com.eventty.eventtynextgen.certification.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.response.CertificationValidateCodeResponseView;
import com.eventty.eventtynextgen.component.EmailSenderService;
import com.eventty.eventtynextgen.shared.utils.CodeGenerator;
import com.eventty.eventtynextgen.component.EmailSenderServiceImpl;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.VerificationErrorType;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {


    private final UserRepository userRepository;
    private final CertificationCodeRepository certificationCodeRepository;
    private final EmailSenderService emailSenderService;

    @Override
    public CertificationExistsResponseView checkExists(String certTarget) {
        return new CertificationExistsResponseView(certTarget, this.userRepository.existsByEmail(certTarget));
    }

    @Override
    public CertificationSendCodeResponseView sendCode(String certTarget) {
        String code = CodeGenerator.generateVerificationCode(EMAIL_VERIFICATION_CODE_LEN);

        CertificationCode certificationCode = CertificationCode.of(certTarget, code, CERTIFICATION_CODE_TTL);
        CertificationCode certificationCodeFromDb = this.certificationCodeRepository.save(certificationCode);
        if (certificationCodeFromDb.getId() != null) {
            this.emailSenderService.sendEmailVerificationMail(certTarget, code);
        } else {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, VerificationErrorType.CERTIFICATION_CODE_SAVE_ERROR);
        }

        return CertificationSendCodeResponseView.createMessage(certTarget, CERTIFICATION_CODE_TTL);
    }

    @Override
    public CertificationValidateCodeResponseView validateCode(String email, String code) {
        boolean validate = true;
        try {
            CertificationCode certificationCode = this.certificationCodeRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> CustomException.badRequest(VerificationErrorType.MISMATCH_EMAIL_VERIFICATION_CODE));

            if (certificationCode.isExpired() || checkExpired(certificationCode)) {
                certificationCode.setExpired();
                throw CustomException.badRequest(VerificationErrorType.EXPIRE_EMAIL_VERIFICATION_CODE);
            }
        } catch (Exception ex) {
            if (ex instanceof CustomException) {
                log.error("인증 코드 검증 중 CustomException 발생 code: {}, errorType: {}", code, ((CustomException) ex).getErrorType());
            } else {
                log.error("인증 코드 검증 중 일반 예외 발생 code: {}", code, ex);
            }
            validate = false;
        }

        return new CertificationValidateCodeResponseView(code, validate);
    }

    private boolean checkExpired(CertificationCode certificationCode) {
        return certificationCode.getExpiredAt().isBefore(LocalDateTime.now());
    }
}
