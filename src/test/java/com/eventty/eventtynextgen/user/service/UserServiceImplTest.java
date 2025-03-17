package com.eventty.eventtynextgen.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service 단위 테스트")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("[GOOD] - 회원 가입 성공")
    void 회원가입_성공() {
        // given
        Long userId = 1L;

        when(userRepository.existsByAuthUserId()).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        Long result = userService.signup(request);

        // then
        assertThat(result).isEqualTo(userId);
    }

}