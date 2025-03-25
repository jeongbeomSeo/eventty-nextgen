package com.eventty.eventtynextgen.auth.redis;

import com.eventty.eventtynextgen.auth.redis.entity.EmailVerification;
import java.util.Optional;

public interface EmailVerificationService {

    EmailVerification saveEmailVerification(String email, String code);

    Optional<EmailVerification> findEmailVerification(String email);
}
