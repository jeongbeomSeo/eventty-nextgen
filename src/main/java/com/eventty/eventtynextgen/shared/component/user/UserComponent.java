package com.eventty.eventtynextgen.shared.component.user;

import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserComponent {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
