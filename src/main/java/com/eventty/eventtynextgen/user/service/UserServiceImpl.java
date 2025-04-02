package com.eventty.eventtynextgen.user.service;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.UserErrorType;
import com.eventty.eventtynextgen.user.model.request.SignupRequest;
import com.eventty.eventtynextgen.user.model.entity.User;
import com.eventty.eventtynextgen.user.model.request.UpdateUserRequest;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.eventty.eventtynextgen.user.service.utils.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw CustomException.of(HttpStatus.CONFLICT, UserErrorType.EMAIL_ALREADY_EXISTS);
        }

        String hashedPassword = passwordEncoder.hashPassword(request.getPassword());

        User user = new User(request.getEmail(), hashedPassword, request.getUserRole(),
            request.getName(), request.getPhone(), request.getBirth());
        user = userRepository.save(user);

        return user.getId();
    }

    @Override
    @Transactional
    public Long updateUser(UpdateUserRequest updateUserRequest) {

        User user = userRepository.findById(updateUserRequest.getId())
            .orElseThrow(
                () -> CustomException.of(HttpStatus.NOT_FOUND, UserErrorType.NOT_FOUND_USER));

        user.update(updateUserRequest.getName(), updateUserRequest.getPhone(), updateUserRequest.getBirth());

        return user.getId();
    }

    @Override
    public Long delete(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> CustomException.badRequest(UserErrorType.NOT_FOUND_USER));

        user.delete();

        return user.getId();
    }
}
