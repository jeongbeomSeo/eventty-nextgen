package com.eventty.eventtynextgen.user.service;

import com.eventty.eventtynextgen.user.model.request.EmailVerificationRequest;
import com.eventty.eventtynextgen.user.model.request.EmailVerificationValidationRequest;
import com.eventty.eventtynextgen.user.model.response.EmailVerificationResponse;
import com.eventty.eventtynextgen.user.redis.EmailVerificationService;
import com.eventty.eventtynextgen.user.redis.entity.EmailVerification;
import com.eventty.eventtynextgen.user.repository.JpaUserRepository;
import com.eventty.eventtynextgen.user.service.utils.CodeGenerator;
import com.eventty.eventtynextgen.user.service.utils.EmailSenderService;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.AuthErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private static final int EMAIL_VERIFICATION_CODE_LEN = 6;

    private final JpaUserRepository userRepository;
    private final CodeGenerator codeGenerator;
    private final EmailVerificationService emailVerificationService;
    private final EmailSenderService emailSenderService;


    @Override
    public boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public EmailVerificationResponse sendEmailVerificationCode(EmailVerificationRequest request) {

        String email = request.getEmail();

        String code = codeGenerator.generateVerificationCode(EMAIL_VERIFICATION_CODE_LEN);

        EmailVerification emailVerification = emailVerificationService.saveEmailVerification(email,
            code);

        emailSenderService.sendEmailVerificationMail(email, code);

        return EmailVerificationResponse.createMessage(email, emailVerification.getTtl());
    }

    @Override
    public boolean checkValidationEmail(EmailVerificationValidationRequest request) {

        EmailVerification emailVerification = emailVerificationService.findEmailVerification(
                request.getEmail())
            .orElseThrow(() -> CustomException.badRequest(AuthErrorType.EXPIRE_EMAIL_VERIFICATION_CODE));

        if (!emailVerification.getCode().equals(request.getCode())) {
            throw CustomException.badRequest(AuthErrorType.MISMATCH_EMAIL_VERIFICATION_CODE);
        }

        emailVerificationService.deleteEmailVerification(emailVerification);

        return true;
    }
}
