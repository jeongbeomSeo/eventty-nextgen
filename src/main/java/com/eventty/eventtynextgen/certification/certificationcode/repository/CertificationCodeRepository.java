package com.eventty.eventtynextgen.certification.certificationcode.repository;

import com.eventty.eventtynextgen.certification.certificationcode.entity.CertificationCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationCodeRepository extends JpaRepository<CertificationCode, Long> {

    Optional<CertificationCode> findByEmailAndCode(String email, String code);
}
