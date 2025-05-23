package com.eventty.eventtynextgen.certification.certificationcode;

import static com.eventty.eventtynextgen.certification.constant.CertificationConst.CERTIFICATION_CODE_TTL;
import static com.eventty.eventtynextgen.certification.constant.CertificationConst.EMAIL_VERIFICATION_CODE_LEN;

import com.eventty.eventtynextgen.certification.certificationcode.entity.CertificationCode;
import com.eventty.eventtynextgen.certification.certificationcode.repository.CertificationCodeRepository;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationExistsResponseView;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationSendCodeResponseView;
import com.eventty.eventtynextgen.certification.certificationcode.response.CertificationValidateCodeResponseView;
import com.eventty.eventtynextgen.component.EmailSenderService;
import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.CertificationErrorType;
import com.eventty.eventtynextgen.shared.utils.CodeGeneratorUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationCodeServiceImpl implements CertificationCodeService {

    private final UserComponent userComponent;
    private final CertificationCodeRepository certificationCodeRepository;
    private final EmailSenderService emailSenderService;

    @Override
    public CertificationExistsResponseView checkExists(String certTarget) {
        return new CertificationExistsResponseView(certTarget, this.userComponent.findByEmail(certTarget).isPresent());
    }

    @Override
    public CertificationSendCodeResponseView sendCode(String certTarget) {
        String code = CodeGeneratorUtil.generateVerificationCode(EMAIL_VERIFICATION_CODE_LEN);

        CertificationCode certificationCode = CertificationCode.of(certTarget, code, CERTIFICATION_CODE_TTL);
        CertificationCode certificationCodeFromDb = this.certificationCodeRepository.save(certificationCode);
        if (certificationCodeFromDb.getId() != null) {
            this.emailSenderService.sendEmailVerificationMail(certTarget, code);
        } else {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, CertificationErrorType.CERTIFICATION_CODE_SAVE_ERROR);
        }

        return CertificationSendCodeResponseView.createMessage(certTarget, CERTIFICATION_CODE_TTL);
    }

    @Override
    public CertificationValidateCodeResponseView validateCode(String email, String code) {
        boolean validate = true;
        try {
            CertificationCode certificationCode = this.certificationCodeRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> CustomException.badRequest(CertificationErrorType.MISMATCH_EMAIL_CERTIFICATION_CODE));

            if (certificationCode.isExpired() || checkExpired(certificationCode)) {
                certificationCode.setExpired();
                throw CustomException.badRequest(CertificationErrorType.EXPIRE_EMAIL_CERTIFICATION_CODE);
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
