package com.eventty.eventtynextgen.user;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.component.PasswordEncoder;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.User.UserStatus;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import com.eventty.eventtynextgen.user.repository.UserRepository;
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
    public UserSignupResponseView signup(String email, String password, UserRoleType userRole, String name, String phone, String birth) {
        if (this.userRepository.existsByEmail(email)) {
            throw CustomException.of(HttpStatus.CONFLICT, UserErrorType.EMAIL_ALREADY_EXISTS);
        }

        User user = User.of(email, this.passwordEncoder.hashPassword(password), userRole, name, phone, birth);
        // 삭제된 유저인지 확인

        User userFromDb = this.userRepository.save(user);
        if (userFromDb.getId() == null) {
            throw CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, UserErrorType.USER_SAVE_ERROR);
        }

        return new UserSignupResponseView(userFromDb.getId(), userFromDb.getEmail());
    }

    @Override
    @Transactional
    public UserUpdateResponseView update(Long userId, String name, String phone, String birth) {
        return this.userRepository.findById(userId).map(user -> {
            // 삭제된 유저인지 확인

            user.updatePersonalInfo(name, phone, birth);
            return new UserUpdateResponseView(user.getId(), user.getName(), user.getPhone(), user.getBirth());
        }).orElseThrow(() -> CustomException.of(HttpStatus.NOT_FOUND, UserErrorType.NOT_FOUND_USER));
    }

    @Override
    @Transactional
    public UserDeleteResponseView delete(Long userId) {
        return this.userRepository.findById(userId).map(user -> {
            // 삭제된 유저인지 확인

            user.updateDeleteStatus(UserStatus.DELETED);
            return new UserDeleteResponseView(userId);
        }).orElseThrow(() -> CustomException.of(HttpStatus.NOT_FOUND, UserErrorType.NOT_FOUND_USER));
    }
}
