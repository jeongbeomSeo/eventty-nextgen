package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.auth.model.dto.request.EmailVerificationRequest;
import com.eventty.eventtynextgen.auth.model.dto.request.EmailVerificationValidationRequest;
import com.eventty.eventtynextgen.auth.model.dto.response.EmailVerificationResponse;

public interface VerificationService {

    boolean checkEmail(String email);
    EmailVerificationResponse sendEmailVerificationCode(EmailVerificationRequest request);

    boolean checkValidationEmail(EmailVerificationValidationRequest request);
}
