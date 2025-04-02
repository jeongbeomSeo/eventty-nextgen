package com.eventty.eventtynextgen.user;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enumtype.UserErrorType;
import com.eventty.eventtynextgen.user.entity.enumtype.UserRole;
import com.eventty.eventtynextgen.user.request.UserSignupRequestCommand;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.request.UserUpdateRequestCommand;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.eventty.eventtynextgen.user.component.PasswordEncoder;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserSignupResponseView signup(String email, String password, UserRole userRole, String name, String phone, String birth) {
        if (this.userRepository.existsByEmail(email)) {
            throw CustomException.of(HttpStatus.CONFLICT, UserErrorType.EMAIL_ALREADY_EXISTS);
        }

        User user = new User(email, this.passwordEncoder.hashPassword(password), userRole, name, phone, birth);
        User userFromDb = this.userRepository.save(user);
        if (userFromDb.getId() == null) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR,
                UserErrorType.USER_SAVE_ERROR);
        }

        return new UserSignupResponseView(userFromDb.getId());
    }

    @Override
    @Transactional
    public UserUpdateResponseView updateUser(Long userId, String name, String phone, String birth) {
        User user = this.userRepository.findById(userId)
            .orElseThrow(
                () -> CustomException.of(HttpStatus.NOT_FOUND, UserErrorType.NOT_FOUND_USER));

        user.update(name, phone, birth);

        return new UserUpdateResponseView(user.getId(), user.getName(), user.getPhone(), user.getBirth());
    }

    @Override
    public UserDeleteResponseView delete(Long userId) {
        User user = this.userRepository.findById(userId)
            .orElseThrow(() -> CustomException.badRequest(UserErrorType.NOT_FOUND_USER));

        user.delete();

        return new UserDeleteResponseView(userId);
    }
}
