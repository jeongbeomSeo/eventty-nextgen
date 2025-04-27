package com.eventty.eventtynextgen.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.UserService;
import com.eventty.eventtynextgen.user.UserServiceImpl;
import com.eventty.eventtynextgen.user.component.PasswordEncoder;
import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.entity.enums.UserRoleType;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import com.eventty.eventtynextgen.user.response.UserActivateDeletedUserResponseView;
import com.eventty.eventtynextgen.user.response.UserDeleteResponseView;
import com.eventty.eventtynextgen.user.response.UserFindAccountResponseView;
import com.eventty.eventtynextgen.user.response.UserSignupResponseView;
import com.eventty.eventtynextgen.user.response.UserUpdateResponseView;
import java.util.List;
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
        @DisplayName("새로운 회원은 회원 가입에 `성공`한다.")
        void 새로운_회원은_회원가입에_성공한다() {
            // given
            String email = "test@google.com";
            String password = "password";

            String hashedPassword = "hashed_password";
            User user = mock(User.class);

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
            when(passwordEncoder.hashPassword(password)).thenReturn(hashedPassword);
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserSignupResponseView userSignupResponseView = userService.signup(
                email,
                password,
                UserRoleType.USER,
                "홍길동",
                "000-0000-0000",
                "1990-01-01");

            // then
            assertThat(userSignupResponseView.userId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("이메일 중복으로 인하여 회원가입에 `실패`한다.")
        void 이메일이_등록되어_있는_경우_회원가입에_실패한다() {
            // given
            String email = "test@google.com";
            User user = mock(User.class);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(false);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.signup(email, "password", UserRoleType.USER, "홍길동", "000-0000-0000", "1990-01-01");
            } catch (CustomException customException) {
                assertThat(customException.getErrorType()).isEqualTo(UserErrorType.EMAIL_ALREADY_EXISTS);
            }
        }

        @Test
        @DisplayName("삭제되어 있는 User가 존재할 경우 `삭제된 계정이 존재한다`는 예외를 전달한다.")
        void 삭제되어_있는_계정이_존재할_경우_이에_맞는_예외를_전달한다() {
            // given
            String email = "test@google.com";
            User user = mock(User.class);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(true);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.signup(email, "password", UserRoleType.USER, "홍길동", "000-0000-0000", "1990-01-01");
            } catch (CustomException customException) {
                assertThat(customException.getErrorType()).isEqualTo(UserErrorType.USER_ALREADY_DELETED);
            }
        }
    }

    @DisplayName("비즈니스 로직 - 회원삭제")
    @Nested
    class Delete {

        @Test
        @DisplayName("id가 일치하는 회원 삭제 요청은 `성공`한다")
        void ID가_일치하는_회원_삭제_요청은_성공한다() {
            // given
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(false);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserDeleteResponseView userDeleteResponseView = userService.delete(userId);

            // then
            assertThat(userDeleteResponseView.userId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("id가 일치하지 않은 회원 삭제 요청은 `실패`한다")
        void ID가_일치하지_않은_회원_삭제_요청은_실패한다() {
            // given
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.delete(userId);
            } catch (CustomException customException) {
                assertThat(customException.getErrorType()).isEqualTo(UserErrorType.NOT_FOUND_USER);
            }
        }

        @Test
        @DisplayName("삭제되어 있는 User가 존재할 경우 삭제 작업에 실패한다.")
        void 이미_삭제되어_있는_경우_회원_삭제_요청은_실패한다() {
            // given
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(true);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.delete(userId);
            } catch (CustomException customException) {
                assertThat(customException.getErrorType()).isEqualTo(UserErrorType.USER_ALREADY_DELETED);
            }
        }
    }

    @DisplayName("비즈니스 로직 - 회원수정")
    @Nested
    class Update {

        @Test
        @DisplayName("id가 일치하는 요청은 `성공`한다.")
        void 조건에_일치하는_회원_수정_요청은_성공한다() {
            // given
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(false);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserUpdateResponseView userUpdateResponseView = userService.update(userId, "변경후이름", "010-1234-5678", "2000-12-12");

            // then
            assertThat(userUpdateResponseView.userId()).isEqualTo(user.getId());
            assertThat(userUpdateResponseView.name()).isEqualTo(user.getName());
            assertThat(userUpdateResponseView.phone()).isEqualTo(user.getPhone());
            assertThat(userUpdateResponseView.birth()).isEqualTo(user.getBirth());
        }

        @Test
        @DisplayName("id가 일치하는 회원이 존재하지 않은 요청은 `실패`한다.")
        void ID가_존재하지_않는_회원_수정_요청은_실패한다() {
            // given
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.update(userId, "변경후이름", "010-1234-5678", "2000-12-12");
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(UserErrorType.NOT_FOUND_USER);
            }
        }

        // TODO: 삭제되어 있는 User를 변경하려고 시도할 경우
        @Test
        @DisplayName("삭제되어 있는 User는 회원 변경 요청에 `실패`한다.")
        void 삭제되어_있는_계정은_회원_변경_요청에_실패한다() {
            // given
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(true);

            UserService userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.update(userId, "변경후이름", "010-1234-5678", "2000-12-12");
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(UserErrorType.USER_ALREADY_DELETED);
            }
        }
    }

    @DisplayName("비즈니스 로직 - 삭제된 회원 활성화")
    @Nested
    class ActivateDeletedUser {

        @Test
        @DisplayName("삭제된 회원일 경우 활성화 요청에 `성공`한다.")
        void 삭제된_회원일_경우_활성화_요청예_성공한다() {
            // given
            Long userId = 1L;
            String email = "test@naver.com";
            String name = "홍길동";

            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(true);
            when(user.getId()).thenReturn(userId);
            when(user.getEmail()).thenReturn(email);
            when(user.getName()).thenReturn(name);

            UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserActivateDeletedUserResponseView result = userService.activateDeletedUser(userId);

            // then
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.email()).isNotBlank();
            assertThat(result.name()).isNotBlank();
        }

        @Test
        @DisplayName("삭제되지 않은 회원일 경우 활성화 요청에 `실패`한다.")
        void 삭제되지_않은_회원일_경우_활성화_요청에_실패한다() {
            // given
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(false);

            UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.activateDeletedUser(userId);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(UserErrorType.USER_NOT_DELETED);
            }
        }

        @Test
        @DisplayName("존재하지 않는 회원일 경우 활성화 요청에 `실패`한다.")
        void 존재하지_않는_회원일_경우_활성화_요청에_실패한다() {
            // given
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.activateDeletedUser(userId);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(UserErrorType.NOT_FOUND_USER);
            }
        }
    }

    @DisplayName("비즈니스 로직 - 계정 찾기")
    @Nested
    class FindAccount {

        @Test
        @DisplayName("인자값으로 들어온 데이터를 통해 활성화되어 있는 사용자를 찾을 경우 요청에 `성공`한다.")
        void 인자값으로_들어온_데이터를_통해_활성_사용자를_찾을_경우_요청에_성공한다() {
            // given
            String name = "홍길동";
            String phone = "010-0000-0000";
            User user = mock(User.class);

            when(user.isDeleted()).thenReturn(false);
            when(userRepository.findByNameAndPhone(name, phone)).thenReturn(List.of(user));

            UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserFindAccountResponseView result = userService.findAccount(name, phone);

            // then
            assertThat(result.accounts().size()).isEqualTo(1);
            assertThat(result.accounts().get(0).userId()).isNotNull();
            assertThat(result.accounts().get(0).email()).isNotBlank();
            assertThat(result.accounts().get(0).isDeleted()).isFalse();
        }

        @Test
        @DisplayName("인자값으로 들어온 데이터를 통해 삭제되어 있는 사용자를 찾을 경우 요청에 `성공`힌디.")
        void 인자값으로_들어온_데이터를_통해_삭제된_사용자를_찾을_경우_요청에_성공한다() {
            // given
            String name = "홍길동";
            String phone = "010-0000-0000";
            User user = mock(User.class);

            when(user.isDeleted()).thenReturn(true);
            when(userRepository.findByNameAndPhone(name, phone)).thenReturn(List.of(user));

            UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserFindAccountResponseView result = userService.findAccount(name, phone);

            // then
            assertThat(result.accounts().size()).isEqualTo(1);
            assertThat(result.accounts().get(0).userId()).isNotNull();
            assertThat(result.accounts().get(0).email()).isNotBlank();
            assertThat(result.accounts().get(0).isDeleted()).isTrue();
        }

        @Test
        @DisplayName("인자값으로 들어온 데이터를 통해 2개 이상의 계정을 찾을 경우 `모든 계정의 정보를 반환`한다.")
        void 인자값으로_들어온_데이터를_통해_다수의_계정을_찾을_경우_모든_계정의_정보를_반환한다() {
            // given
            String name = "홍길동";
            String phone = "010-0000-0000";
            User user1 = mock(User.class);
            User user2 = mock(User.class);

            when(user1.isDeleted()).thenReturn(false);
            when(user2.isDeleted()).thenReturn(true);
            when(userRepository.findByNameAndPhone(name, phone)).thenReturn(List.of(user1, user2));


            UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when
            UserFindAccountResponseView result = userService.findAccount(name, phone);

            // then
            assertThat(result.accounts().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("인자값으로 들어온 데이터를 통해 사용자를 찾지 못했을 경우 요청에 `성공`한다.")
        void 인자값으로_들어온_데이터를_통해_사용자를_찾지_못했을_경우_요청에_실패한다() {
            // given
            String name = "홍길동";
            String phone = "010-0000-0000";

            when(userRepository.findByNameAndPhone(name, phone)).thenReturn(List.of());

            UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

            // when & then
            try {
                userService.findAccount(name, phone);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(UserErrorType.NOT_FOUND_USER);
            }
        }

    }
}