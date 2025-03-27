package com.eventty.eventtynextgen.user.service;

import com.eventty.eventtynextgen.user.model.request.EmailVerificationRequest;
import com.eventty.eventtynextgen.user.model.request.EmailVerificationValidationRequest;
import com.eventty.eventtynextgen.user.model.response.EmailVerificationResponse;

public interface VerificationService {

    boolean existsEmail(String email);

    EmailVerificationResponse sendEmailVerificationCode(EmailVerificationRequest request);

    boolean checkValidationEmail(EmailVerificationValidationRequest request);
}
