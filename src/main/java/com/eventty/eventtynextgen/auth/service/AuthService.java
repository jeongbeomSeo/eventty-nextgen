package com.eventty.eventtynextgen.auth.service;

import com.eventty.eventtynextgen.auth.model.dto.request.EmailVerificationValidationRequest;
import com.eventty.eventtynextgen.auth.model.dto.request.SignupRequest;
import com.eventty.eventtynextgen.auth.model.dto.response.EmailVerificationResponse;

public interface AuthService {

    Long signup(SignupRequest request);

    boolean checkEmail(String email);

    Long delete(Long authUserId);

    EmailVerificationResponse sendEmailVerificationCode(String email);

    boolean checkValidationEmail(EmailVerificationValidationRequest request);
}
