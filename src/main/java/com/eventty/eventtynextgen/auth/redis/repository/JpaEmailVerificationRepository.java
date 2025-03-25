package com.eventty.eventtynextgen.auth.redis.repository;

import com.eventty.eventtynextgen.auth.redis.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEmailVerificationRepository extends JpaRepository<EmailVerification, String> {

}
