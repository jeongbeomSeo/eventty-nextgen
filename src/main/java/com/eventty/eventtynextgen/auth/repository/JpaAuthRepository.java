package com.eventty.eventtynextgen.auth.repository;

import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAuthRepository extends JpaRepository<AuthUser, Long>, AuthRepository {

    @Override
    boolean existsByEmail(String email);
}
