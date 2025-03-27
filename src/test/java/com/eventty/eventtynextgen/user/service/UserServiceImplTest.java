package com.eventty.eventtynextgen.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.type.UserErrorType;
import com.eventty.eventtynextgen.user.fixture.SignupRequestFixture;
import com.eventty.eventtynextgen.user.fixture.UpdateUserRequestFixture;
import com.eventty.eventtynextgen.user.fixture.UserFixture;
import com.eventty.eventtynextgen.user.model.entity.User;
import com.eventty.eventtynextgen.user.model.request.SignupRequest;
import com.eventty.eventtynextgen.user.model.request.UpdateUserRequest;
import com.eventty.eventtynextgen.user.repository.JpaUserRepository;
import com.eventty.eventtynextgen.user.service.utils.PasswordEncoder;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service 단위 테스트")
class UserServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("비즈니스 로직 - 회원가입")
    @Nested
    class Signup {

        @Test
        @DisplayName("user signup - 새로운 회원은 회원 가입에 `성공`한다.")
        void 새로운_회원은_회원가입에_성공한다() {
            // given
            SignupRequest request = SignupRequestFixture.successUserRoleRequest();

            User user = UserFixture.createUserBySignupRequest(request);

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.hashPassword(request.getPassword())).thenReturn("hashed_password");
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            Long result = userService.signup(request);

            // then
            assertThat(result).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("user signup - 이메일 중복으로 인하여 회원가입에 `실패`한다.")
        void 이메일이_등록되어_있는_경우_회원가입에_실패한다() {
            // given
            SignupRequest request = SignupRequestFixture.successUserRoleRequest();

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.signup(request);
            } catch (CustomException customException) {
                assertThat(customException.getErrorType())
                    .isEqualTo(UserErrorType.EMAIL_ALREADY_EXISTS);
            }
        }
    }

    @DisplayName("비즈니스 로직 - 회원삭제")
    @Nested
    class DeleteTest {

        @Test
        @DisplayName("auth user delete - id가 일치하는 회원 삭제 요청은 `성공`한다")
        void ID가_일치하는_회원_삭제_요청은_성공한다() {
            // given
            User user = UserFixture.createBaseUser();
            Long userId = user.getId();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            Long deletedAuthUserId = userService.delete(userId);

            // then
            assertThat(deletedAuthUserId).isEqualTo(userId);
        }

        @Test
        @DisplayName("auth user delete - id가 일치하지 않은 회원 삭제 요청은 `실패`한다")
        void ID가_일치하지_않은_회원_삭제_요청은_실패한다() {
            // given
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.delete(userId);
            } catch (CustomException customException) {
                assertThat(customException.getErrorType())
                    .isEqualTo(UserErrorType.NOT_FOUND_USER);
            }
        }


    }

    @DisplayName("비즈니스 로직 - 회원수정")
    @Nested
    class updateUser {

        @Test
        @DisplayName("user update - id가 일치하는 요청은 `성공`한다.")
        void 조건에_일치하는_회원_수정_요청은_성공한다() {
            // given
            UpdateUserRequest updateUserRequest = UpdateUserRequestFixture.basicUpdateRequest();
            User user = UserFixture.createUserByUpdateUserRequest(updateUserRequest);

            when(userRepository.findById(updateUserRequest.getId())).thenReturn(Optional.of(user));
            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            Long result = userService.updateUser(updateUserRequest);

            // then
            assertThat(result).isEqualTo(updateUserRequest.getId());
        }

        @Test
        @DisplayName("user update - id가 일치하는 회원이 존재하지 않은 요청은 `실패`한다.")
        void ID가_존재하지_않는_회원_수정_요청은_실패한다() {
            // given
            UpdateUserRequest updateUserRequest = UpdateUserRequestFixture.basicUpdateRequest();

            when(userRepository.findById(updateUserRequest.getId())).thenReturn(Optional.empty());
            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.updateUser(updateUserRequest);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType())
                    .isEqualTo(UserErrorType.NOT_FOUND_USER);
            }
        }
    }

}