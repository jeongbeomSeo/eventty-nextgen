package com.eventty.eventtynextgen.user;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.utils.PasswordEncoder;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.User.UserStatus;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.eventty.eventtynextgen.user.response.UserActivateDeletedUserResponseView;
import com.eventty.eventtynextgen.user.response.UserChangePasswordResponseView;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserFindEmailResponseView;
import com.eventty.eventtynextgen.user.response.UserFindEmailResponseView.UserEmailInfo;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserSignupResponseView signup(String email, String password, UserRoleType userRole, String name, String phone, String birth) {
        this.userRepository.findByEmail(email).ifPresent(user -> {
            if (!user.isDeleted()) {
                throw CustomException.of(HttpStatus.CONFLICT, UserErrorType.EMAIL_ALREADY_EXISTS);
            } else {
                throw CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED);
            }
        });

        User user = User.of(email, PasswordEncoder.encode(password), userRole, name, phone, birth);
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
            if (user.isDeleted()) {
                throw CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED);
            }

            user.updatePersonalInfo(name, phone, birth);
            return new UserUpdateResponseView(user.getId(), user.getName(), user.getPhone(), user.getBirth());
        }).orElseThrow(() -> CustomException.of(HttpStatus.NOT_FOUND, UserErrorType.NOT_FOUND_USER));
    }

    @Override
    @Transactional
    public UserDeleteResponseView delete(Long userId) {
        return this.userRepository.findById(userId).map(user -> {
            if (user.isDeleted()) {
                throw CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED);
            }

            user.updateDeleteStatus(UserStatus.DELETED);
            return new UserDeleteResponseView(userId);
        }).orElseThrow(() -> CustomException.of(HttpStatus.NOT_FOUND, UserErrorType.NOT_FOUND_USER));
    }

    @Override
    public UserActivateDeletedUserResponseView activateToDeletedUser(Long userId) {
        return this.userRepository.findById(userId).map(user -> {
            if (!user.isDeleted()) {
                throw CustomException.badRequest(UserErrorType.USER_NOT_DELETED);
            }

            user.updateDeleteStatus(UserStatus.ACTIVE);
            return new UserActivateDeletedUserResponseView(user.getId(), user.getEmail(), user.getName());
        }).orElseThrow(() -> CustomException.badRequest(UserErrorType.NOT_FOUND_USER));
    }

    @Override
    public UserFindEmailResponseView findEmailByPersonalInfo(String name, String phone) {
        List<UserEmailInfo> emailInfos = this.userRepository.findByNameAndPhone(name, phone).stream()
            .filter(user -> !user.isDeleted())
            .map(user -> new UserEmailInfo(user.getId(), user.getEmail()))
            .toList();

        return new UserFindEmailResponseView(emailInfos);
    }

    @Override
    @Transactional
    public UserChangePasswordResponseView changePassword(Long userId, String currentPassword, String updatedPassword) {
        return this.userRepository.findById(userId).map(user -> {
            if (user.isDeleted()) {
                throw CustomException.badRequest(UserErrorType.USER_ALREADY_DELETED);
            }

            if (!PasswordEncoder.matches(currentPassword, user.getPassword())) {
                throw CustomException.badRequest(UserErrorType.MISMATCH_CURRENT_PASSWORD);
            }

            user.changePassword(PasswordEncoder.encode(updatedPassword));

            return new UserChangePasswordResponseView(user.getId(), user.getName(), user.getEmail());
        }).orElseThrow(() -> CustomException.badRequest(UserErrorType.NOT_FOUND_USER));
    }
}
