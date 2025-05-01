package com.eventty.eventtynextgen.user.repository;

import com.eventty.eventtynextgen.user.entity.User;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByNameAndPhone(String name, String phone);
}
