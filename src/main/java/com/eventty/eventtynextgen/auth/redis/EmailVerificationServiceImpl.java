package com.eventty.eventtynextgen.auth.redis;

import com.eventty.eventtynextgen.auth.redis.entity.EmailVerification;
import com.eventty.eventtynextgen.auth.redis.repository.JpaEmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService{

    private final JpaEmailVerificationRepository emailVerificationRepository;

    @Override
    public EmailVerification saveEmailVerification(String email, String code) {
        EmailVerification emailVerification = new EmailVerification(email, code);

        return emailVerificationRepository.save(emailVerification);
    }
}
