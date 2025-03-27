package com.eventty.eventtynextgen.user.redis;

import com.eventty.eventtynextgen.user.redis.entity.EmailVerification;
import java.util.Optional;

public interface EmailVerificationService {

    EmailVerification saveEmailVerification(String email, String code);

    Optional<EmailVerification> findEmailVerification(String email);

    void deleteEmailVerification(EmailVerification emailVerification);
}
