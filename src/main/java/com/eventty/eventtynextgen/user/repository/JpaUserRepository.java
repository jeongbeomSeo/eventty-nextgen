package com.eventty.eventtynextgen.user.repository;

import com.eventty.eventtynextgen.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    boolean existsByAuthUserId(Long authUserId);

}
