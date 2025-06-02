package com.eventty.eventtynextgen.certification.authuser.repository;

import com.eventty.eventtynextgen.certification.authuser.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

}
