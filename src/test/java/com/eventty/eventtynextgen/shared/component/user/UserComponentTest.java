package com.eventty.eventtynextgen.shared.component.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.user.entity.User;
import com.eventty.eventtynextgen.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserComponent 단위 테스트")
class UserComponentTest {

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("Email로 활성화 상태인 유저 찾기")
    class GetActivatedUserByEmail {

        @Test
        @DisplayName("인자로 들어온 Email을 가진 활성화 유저가 존재할 경우 해당 User를 반환한다.")
        void 인자로_들어온_Email을_가진_활성화_유저가_존재할_경우_해당_USER를_반환한다() {
            // given
            String email = "testEmail@gmail.com";
            User user = mock(User.class);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(false);

            UserComponent userComponent = new UserComponent(userRepository);

            // when
            Optional<User> activatedUser = userComponent.getActivatedUserByEmail(email);

            // then
            assertThat(activatedUser.get()).isEqualTo(user);
        }

        @Test
        @DisplayName("인자로 들어온 Email을 가진 삭제된 유저가 존재할 경우 빈값을 반환한다.")
        void 인자로_들어온_Email을_가진_삭제된_유저가_존재할_경우_빈값을_반환한다() {
            // given
            String email = "testEmail@gmail.com";
            User user = mock(User.class);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(true);

            UserComponent userComponent = new UserComponent(userRepository);

            // when
            Optional<User> activatedUser = userComponent.getActivatedUserByEmail(email);

            // then
            assertThat(activatedUser.isEmpty()).isEqualTo(true);
        }

        @Test
        @DisplayName("인자로 들어온 Email을 가진 유저가 없을 경우 빈값을 반환한다")
        void 인자로_들어온_Email을_가진_유저가_없을_경우_빈값을_반환한다() {
            // given
            String email = "testEmail@gmail.com";

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            UserComponent userComponent = new UserComponent(userRepository);

            // when
            Optional<User> activatedUser = userComponent.getActivatedUserByEmail(email);

            // then
            assertThat(activatedUser.isEmpty()).isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("UserId로 활성화 상태인 유저 찾기")
    class GetActivatedUserByUserId {

        @Test
        @DisplayName("인자로 들어온 UserId을 가진 활성화 유저가 존재할 경우 해당 User를 반환한다.")
        void 인자로_들어온_USER_ID를_가진_활성화_유저가_존재할_경우_해당_USER를_반환한다() {
            // given
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(false);

            UserComponent userComponent = new UserComponent(userRepository);

            // when
            Optional<User> activatedUser = userComponent.getActivatedUserByUserId(userId);

            // then
            assertThat(activatedUser.get()).isEqualTo(user);
        }

        @Test
        @DisplayName("인자로 들어온 UserId를 가진 활성화 유저가 존재하지 않을 경우 빈값을 반환한다.")
        void 인자로_들어온_USER_ID를_가진_활성화_유저가_존재하지_않을_경우_빈값을_반환한다() {
            // given
            Long userId = 1L;
            User user = mock(User.class);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.isDeleted()).thenReturn(true);

            UserComponent userComponent = new UserComponent(userRepository);

            // when
            Optional<User> activatedUser = userComponent.getActivatedUserByUserId(userId);

            // then
            assertThat(activatedUser.isEmpty()).isEqualTo(true);
        }

        @Test
        @DisplayName("인자로 들어온 UserId를 가진 유저가 없을 경우 빈값을 반환한다")
        void 인자로_들어온_USER_ID를_가진_유저가_없을_경우_빈값을_반환한다() {
            // given
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            UserComponent userComponent = new UserComponent(userRepository);

            // when
            Optional<User> activatedUser = userComponent.getActivatedUserByUserId(userId);

            // then
            assertThat(activatedUser.isEmpty()).isEqualTo(true);
        }
    }
}