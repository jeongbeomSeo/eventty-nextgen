package com.eventty.eventtynextgen.auth.redis;

import com.eventty.eventtynextgen.auth.redis.entity.EmailVerification;

public interface EmailVerificationService {

    EmailVerification saveEmailVerification(String email, String code);
}
