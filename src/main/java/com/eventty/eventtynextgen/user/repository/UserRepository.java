package com.eventty.eventtynextgen.user.repository;

import com.eventty.eventtynextgen.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
