package com.eventty.eventtynextgen.certification.repository;

import com.eventty.eventtynextgen.certification.entity.CertificationCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationCodeRepository extends JpaRepository<CertificationCode, Long> {

    Optional<CertificationCode> findByCode(String email);
}
