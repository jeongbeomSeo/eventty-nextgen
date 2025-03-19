package com.eventty.eventtynextgen.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.eventty.eventtynextgen.auth.fixture.UserFixture;
import com.eventty.eventtynextgen.auth.fixture.UserSignupRequestFixture;
import com.eventty.eventtynextgen.shared.model.dto.request.UserSignupRequest;
import com.eventty.eventtynextgen.user.model.entity.User;
import com.eventty.eventtynextgen.user.repository.JpaUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service 단위 테스트")
class UserServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;

    @Test
    @DisplayName("[GOOD] - 회원 가입 성공")
    void 회원가입_성공() {
        // given
        UserSignupRequest request = UserSignupRequestFixture.successUserSignupRequest();
        User user = UserFixture.createUserByUserSignupRequest(request);

        when(userRepository.existsByAuthUserId(request.getAuthUserId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserService userService = new UserServiceImpl(userRepository);

        // when
        Long result = userService.signup(request);

        // then
        assertThat(result).isEqualTo(user.getId());
    }

}