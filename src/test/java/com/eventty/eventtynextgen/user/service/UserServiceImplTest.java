package com.eventty.eventtynextgen.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.UserService;
import com.eventty.eventtynextgen.user.UserServiceImpl;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.eventty.eventtynextgen.user.component.PasswordEncoder;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;
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
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("비즈니스 로직 - 회원가입")
    @Nested
    class Signup {
        @Test
        @DisplayName("user signup - 새로운 회원은 회원 가입에 `성공`한다.")
        void 새로운_회원은_회원가입에_성공한다() {
            // given
            String email = "test@google.com";
            String password = "password";

            String hashedPassword = "hashed_password";
            User user = mock(User.class);

            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(passwordEncoder.hashPassword(password)).thenReturn(hashedPassword);
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserSignupResponseView userSignupResponseView = userService.signup(email, password, UserRoleType.USER, "홍길동",
                "000-0000-0000", "1990-01-01");

            // then
            assertThat(userSignupResponseView.userId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("user signup - 이메일 중복으로 인하여 회원가입에 `실패`한다.")
        void 이메일이_등록되어_있는_경우_회원가입에_실패한다() {
            // given
            String email = "test@google.com";

            when(userRepository.existsByEmail(email)).thenReturn(true);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.signup(email, "password", UserRoleType.USER, "홍길동", "000-0000-0000", "1990-01-01");
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
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserDeleteResponseView userDeleteResponseView = userService.delete(userId);

            // then
            assertThat(userDeleteResponseView.userId()).isEqualTo(userId);
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
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserUpdateResponseView userUpdateResponseView = userService.update(userId, "변경후이름",
                "010-1234-5678", "2000-12-12");

            // then
            assertThat(userUpdateResponseView.userId()).isEqualTo(user.getId());
            assertThat(userUpdateResponseView.name()).isEqualTo(user.getName());
            assertThat(userUpdateResponseView.phone()).isEqualTo(user.getPhone());
            assertThat(userUpdateResponseView.birth()).isEqualTo(user.getBirth());
        }

        @Test
        @DisplayName("user update - id가 일치하는 회원이 존재하지 않은 요청은 `실패`한다.")
        void ID가_존재하지_않는_회원_수정_요청은_실패한다() {
            // given
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.update(userId, "변경후이름",  "010-1234-5678", "2000-12-12");
            } catch (CustomException ex) {
                assertThat(ex.getErrorType())
                    .isEqualTo(UserErrorType.NOT_FOUND_USER);
            }
        }
    }
}