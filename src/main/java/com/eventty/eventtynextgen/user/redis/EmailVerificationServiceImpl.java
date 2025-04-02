package com.eventty.eventtynextgen.user.redis;

import com.eventty.eventtynextgen.user.redis.entity.EmailVerification;
import com.eventty.eventtynextgen.user.redis.repository.JpaEmailVerificationRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService{

    private final JpaEmailVerificationRepository emailVerificationRepository;

    @Override
    public EmailVerification saveEmailVerification(String email, String code) {
        EmailVerification emailVerification = EmailVerification.createEntity(email, code);

        return emailVerificationRepository.save(emailVerification);
    }

    @Override
    public Optional<EmailVerification> findEmailVerification(String email) {
        return emailVerificationRepository.findByEmail(email);
    }

    @Override
    public void deleteEmailVerification(EmailVerification emailVerification) {
        emailVerificationRepository.delete(emailVerification);
    }
}
