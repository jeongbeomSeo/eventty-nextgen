package com.eventty.eventtynextgen.auth.redis.repository;

import com.eventty.eventtynextgen.auth.redis.entity.EmailVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEmailVerificationRepository extends JpaRepository<EmailVerification, String> {

    Optional<EmailVerification> findByEmail(String email);

}
