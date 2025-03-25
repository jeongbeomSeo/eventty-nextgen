package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.auth.model.dto.request.EmailVerificationValidationRequest;
import com.eventty.eventtynextgen.auth.model.dto.response.EmailVerificationResponse;
import com.eventty.eventtynextgen.auth.redis.EmailVerificationService;
import com.eventty.eventtynextgen.auth.redis.entity.EmailVerification;
import com.eventty.eventtynextgen.auth.repository.JpaAuthRepository;
import com.eventty.eventtynextgen.auth.service.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private static final int EMAIL_VERIFICATION_CODE_LEN = 6;

    private final JpaAuthRepository authRepository;
    private final CodeGenerator codeGenerator;
    private final EmailVerificationService emailVerificationService;


    @Override
    public boolean checkEmail(String email) {
        return authRepository.existsByEmail(email);
    }

    @Override
    public EmailVerificationResponse sendEmailVerificationCode(String email) {

        String code = codeGenerator.generateVerificationCode(EMAIL_VERIFICATION_CODE_LEN);

        EmailVerification emailVerification = emailVerificationService.saveEmailVerification(email,
            code);



        return null;
    }

    @Override
    public boolean checkValidationEmail(EmailVerificationValidationRequest request) {
        return false;
    }
}
