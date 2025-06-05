package com.eventty.eventtynextgen.auth.authcode.repository;

import com.eventty.eventtynextgen.auth.authcode.entity.AuthCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthCodeRepository extends JpaRepository<AuthCode, Long> {

    Optional<AuthCode> findByEmailAndCode(String email, String code);
}
