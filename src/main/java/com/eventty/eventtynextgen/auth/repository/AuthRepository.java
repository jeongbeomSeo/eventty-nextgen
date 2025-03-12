package com.eventty.eventtynextgen.auth.repository;

import com.eventty.eventtynextgen.auth.model.entity.AuthUser;
import org.springframework.stereotype.Component;

public interface AuthRepository {
    AuthUser save(AuthUser authUser);

    boolean existsByEmail(String email);
}
