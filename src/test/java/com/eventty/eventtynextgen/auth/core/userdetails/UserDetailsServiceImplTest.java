package com.eventty.eventtynextgen.auth.core.userdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.shared.component.user.UserComponent;
import com.eventty.eventtynextgen.shared.exception.CustomException;
import com.eventty.eventtynextgen.shared.exception.enums.UserErrorType;
import com.eventty.eventtynextgen.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl 단위 테스트")
class UserDetailsServiceImplTest {

    @Mock
    private UserComponent userComponent;

    @Nested
    @DisplayName("UserDetails 조회")
    class LoadUserDetailsByLoginId {

        @Test
        @DisplayName("이메일을 통해 사용자 조회에 성공할 경우 UserDetails 객체 생성에 성공한다.")
        void 이메일을_통해_사용자_조회에_성공할_경우_객체_생성에_성공한다() {
            // given
            String loginId = "example@gmail.com";
            User userFromDb = Mockito.mock(User.class);

            when(userComponent.getUserByEmail(loginId)).thenReturn(Optional.of(userFromDb));
            when(userFromDb.getId()).thenReturn(1L);
            when(userFromDb.getEmail()).thenReturn(loginId);
            when(userFromDb.getPassword()).thenReturn("encoded_password");
            when(userFromDb.isDeleted()).thenReturn(false);

            UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userComponent);

            // when
            UserDetails userDetails = userDetailsService.loadUserDetailsByLoginId(loginId);

            // then
            assertThat(userDetails.getUserId()).isNotNull();
            assertThat(userDetails.getLoginId()).isNotBlank();
            assertThat(userDetails.getPassword()).isNotBlank();
            assertThat(userDetails.isActive()).isTrue();
        }

        @Test
        @DisplayName("이메일을 통해 사용자 조회에 실패할 경우 예외를 발생시킨다.")
        void 이메일을_통해_사용자_조회에_실패할_경우_예외를_발생시킨다() {
            // given
            String loginId = "example@gmail.com";

            when(userComponent.getUserByEmail(loginId)).thenReturn(Optional.empty());

            UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userComponent);

            // when & then
            try {
                userDetailsService.loadUserDetailsByLoginId(loginId);
            } catch (CustomException ex) {
                assertThat(ex.getErrorType()).isEqualTo(UserErrorType.NOT_FOUND_USER);
            }
        }
    }
}